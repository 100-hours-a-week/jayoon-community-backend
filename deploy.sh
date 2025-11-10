#!/bin/bash

# --- (1) 사용자 설정 ---
PEM_KEY="~/Downloads/community.pem"
EC2_HOST="3.38.115.200"
EC2_USER="ubuntu"

# --- (2) 프로젝트 경로 설정 ---
BE_PROJECT_PATH=$(pwd) # 이 스크립트는 BE 프로젝트 루트에서 실행

# dev 모드용 경로
BE_BUILD_DIR="$BE_PROJECT_PATH/build/libs"
REMOTE_BE_APP_DIR="/home/$EC2_USER/app/be"
NGINX_CONFIG_FILE_PATH="$BE_PROJECT_PATH/scripts/config/nginx-default"
FE_PROJECT_PATH="$BE_PROJECT_PATH/../community-frontend/public"
REMOTE_FE_STAGING_DIR="/home/$EC2_USER/app/fe-staging"
REMOTE_FE_DEPLOY_DIR="/var/www/html"

# init 모드용 경로 (★ 수정 ★)
EC2_INIT_SCRIPT_PATH="$BE_PROJECT_PATH/scripts/init-ec2.sh"
REMOTE_INIT_SCRIPT_PATH="/home/$EC2_USER/init-ec2.sh"


# --- (3) 명령어 인자(Argument) 파싱 ---
MODE=$1 # $1은 첫 번째 인자 (e.g., 'init' or 'dev')

if [ "$MODE" == "init" ]; then
    # ======================================================
    # ---          (A) EC2 초기 설정(init) 모드          ---
    # ======================================================

    echo "========================================"
    echo ">> EC2 초기 설정(init) 모드를 시작합니다..."
    echo "========================================"

    echo ">> (1/2) init-ec2.sh 파일을 EC2로 전송합니다..."
    scp -i "$PEM_KEY" "$EC2_INIT_SCRIPT_PATH" "$EC2_USER@$EC2_HOST:$REMOTE_INIT_SCRIPT_PATH"
    if [ $? -ne 0 ]; then exit 1; fi

    echo ">> (2/2) EC2에서 초기 설정 스크립트를 실행합니다..."
    echo "     (sudo 권한으로 실행되며, 시간이 오래 걸릴 수 있습니다.)"

    # EC2에 접속해서 스크립트 실행
    ssh -i "$PEM_KEY" "$EC2_USER@$EC2_HOST" "
        set -e
        echo '>>> 스크립트 실행 권한을 부여합니다 (chmod +x)'
        chmod +x $REMOTE_INIT_SCRIPT_PATH

        echo '>>> 스크립트를 sudo로 실행합니다...'
        sudo $REMOTE_INIT_SCRIPT_PATH

        echo '>>> 원격 스크립트 실행 완료.'
    "
    if [ $? -ne 0 ]; then
        echo " 🚨 EC2에서 설정 스크립트 실행에 실패했습니다."
        exit 1
    fi

    echo ">> ✅ EC2 초기 설정(init)이 완료되었습니다."
    echo "   (이제 EC2에 접속하여 MySQL 수동 설정을 완료하세요)"

elif [ "$MODE" == "dev" ]; then
    # ======================================================
    # ---          (B) 애플리케이션 배포(dev) 모드          ---
    # ======================================================

    echo "========================================"
    echo ">> 빅뱅 배포(dev) 모드를 시작합니다..."
    echo "========================================"

    # 1. BE 프로젝트 빌드
    echo " (1/7) Spring Boot 애플리케이션을 빌드합니다..."
    cd $BE_PROJECT_PATH
    ./gradlew clean build
    if [ $? -ne 0 ]; then echo " 🚨 BE 빌드 실패"; exit 1; fi
    echo " ✅ BE 빌드가 완료되었습니다."

    # 2. 빌드된 BE JAR 파일 찾기
    echo " (2/7) BE .jar 파일을 찾습니다..."
    JAR_FILE=$(find "$BE_BUILD_DIR" -name "*.jar" -not -name "*-plain.jar" | head -n 1)
    if [ -z "$JAR_FILE" ]; then echo " 🚨 .jar 파일 없음"; exit 1; fi
    JAR_NAME=$(basename "$JAR_FILE")
    echo " ✅ BE 배포 대상: $JAR_NAME"

    # 3. FE 'public' 폴더 존재 여부 확인
    echo " (3/7) FE 'public' 폴더를 찾습니다..."
    if [ ! -d "$FE_PROJECT_PATH" ]; then echo " 🚨 FE 폴더 없음"; exit 1; fi
    echo " ✅ FE 배포 대상: '$FE_PROJECT_PATH' 폴더의 *내용물*"

    # 4. Nginx 설정 파일 존재 여부 확인
    echo " (4/7) Nginx 설정 파일을 찾습니다..."
    if [ ! -f "$NGINX_CONFIG_FILE_PATH" ]; then echo " 🚨 Nginx 설정 파일 없음"; exit 1; fi
    echo " ✅ Nginx 설정 파일 확인 완료."

    # 5. EC2로 BE/FE/Nginx-Config 파일 전송
    echo " (5/7) BE, FE, Nginx-Config 파일을 EC2 인스턴스로 전송합니다..."
    ssh -i "$PEM_KEY" "$EC2_USER@$EC2_HOST" "mkdir -p $REMOTE_BE_APP_DIR && mkdir -p $REMOTE_FE_STAGING_DIR"
    scp -i "$PEM_KEY" -r "$FE_PROJECT_PATH"/* "$EC2_USER@$EC2_HOST:$REMOTE_FE_STAGING_DIR/"
    scp -i "$PEM_KEY" "$JAR_FILE" "$EC2_USER@$EC2_HOST:$REMOTE_BE_APP_DIR/$JAR_NAME"
    scp -i "$PEM_KEY" "$NGINX_CONFIG_FILE_PATH" "$EC2_USER@$EC2_HOST:$REMOTE_FE_STAGING_DIR/nginx-default"
    if [ $? -ne 0 ]; then echo " 🚨 파일 전송 실패"; exit 1; fi
    echo " ✅ 파일 전송이 완료되었습니다."

    # 6. EC2에 접속하여 배포 실행
    echo " (6/7) EC2에 접속하여 BE 중지, FE/Nginx 배포, BE 재시작..."
    ssh -i "$PEM_KEY" "$EC2_USER@$EC2_HOST" "
        set -e
        BE_APP_PORT=8080
        BE_JAR_PATH=\"$REMOTE_BE_APP_DIR/$JAR_NAME\"
        BE_LOG_PATH=\"$REMOTE_BE_APP_DIR/app.log\"
        FE_STAGING_PATH=\"$REMOTE_FE_STAGING_DIR\"
        FE_DEPLOY_PATH=\"$REMOTE_FE_DEPLOY_DIR\"
        NGINX_CONFIG_STAGING_PATH=\"$REMOTE_FE_STAGING_DIR/nginx-default\"
        NGINX_CONFIG_DEPLOY_PATH=\"/etc/nginx/sites-available/default\"

        echo '>> (BE) 기존 프로세스 종료'
        fuser -k -n tcp \$BE_APP_PORT || true
        sleep 2

        echo '>> (Nginx) 설정 파일 배포'
        sudo cp \$NGINX_CONFIG_STAGING_PATH \$NGINX_CONFIG_DEPLOY_PATH
        sudo rm -f \$NGINX_CONFIG_STAGING_PATH

        echo '>> (FE) 파일 배포'
        sudo rm -rf \$FE_DEPLOY_PATH/*
        sudo cp -r \$FE_STAGING_PATH/* \$FE_DEPLOY_PATH/

        echo '>> (Nginx) 서비스 재시작'
        sudo systemctl restart nginx

        echo '>> (BE) 새 애플리케이션 실행'
        nohup java -jar \$BE_JAR_PATH > \$BE_LOG_PATH 2>&1 &
    "
    #
    if [ $? -ne 0 ]; then echo " 🚨 EC2에서 배포 스크립트 실패"; exit 1; fi

    # 7. 배포 완료
    echo " (7/7) 모든 배포 과정이 완료되었습니다. 🎉"
    echo " EC2 인스턴스(${EC2_HOST})에서 애플리케이션이 실행 중입니다."
    echo " 로그 확인: ssh -i $PEM_KEY $EC2_USER@$EC2_HOST tail -f $REMOTE_BE_APP_DIR/app.log"

else
    # ======================================================
    # ---          (C) 잘못된 모드          ---
    # ======================================================
    echo "🚨 잘못된 사용법입니다."
    echo ""
    echo "   EC2 초기 설정이 필요할 때:"
    echo "   sh deploy.sh init"
    echo ""
    echo "   애플리케이션을 배포할 때:"
    echo "   sh deploy.sh dev"
    exit 1
fi