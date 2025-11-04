#!/bin/bash

# --- (1) 사용자 설정 ---
PEM_KEY="~/Downloads/community.pem"           # EC2 접속용 .pem 키 경로
EC2_HOST="52.23.231.146"                      # EC2 인스턴스 Public DNS 또는 IP 주소
EC2_USER="ubuntu"                             # EC2 사용자 이름

# --- (2) 프로젝트 설정 ---
PROJECT_PATH=$(pwd)
BUILD_DIR="$PROJECT_PATH/build/libs"
REMOTE_APP_DIR="/home/$EC2_USER/app"              # EC2 인스턴스에 애플리케이션을 저장할 디렉토리

# --- (3) 스크립트 시작 ---
echo " 배포를 시작합니다... 🚀"

# 1. 프로젝트 빌드
echo " (1/5) Spring Boot 애플리케이션을 빌드합니다... (./gradlew clean build)"
./gradlew clean build
if [ $? -ne 0 ]; then
    echo " 🚨 Gradle 빌드에 실패했습니다. 스크립트를 중단합니다."
    exit 1
fi
echo " ✅ 빌드가 완료되었습니다."

# 2. 빌드된 '실행 가능(fat)' JAR 파일 찾기
echo " (2/5) 실행 가능한 JAR 파일을 '$BUILD_DIR'에서 찾습니다..."
# '*-plain.jar' 파일을 제외하고 실행 가능한 jar 파일만 찾도록 수정
JAR_FILE=$(find "$BUILD_DIR" -name "*.jar" -not -name "*-plain.jar" | head -n 1)

if [ -z "$JAR_FILE" ]; then
    echo " 🚨 '$BUILD_DIR'에서 실행 가능한 .jar 파일을 찾을 수 없습니다."
    echo "    (community-0.0.1-SNAPSHOT.jar 같은 파일이 필요합니다.)"
    exit 1
fi

JAR_NAME=$(basename "$JAR_FILE")
echo " ✅ 배포 대상 파일: $JAR_NAME"

# 3. 빌드된 JAR 파일을 EC2로 전송
echo " (3/5) JAR 파일을 EC2 인스턴스로 전송합니다..."
scp -i "$PEM_KEY" "$JAR_FILE" "$EC2_USER@$EC2_HOST:$REMOTE_APP_DIR/$JAR_NAME"
if [ $? -ne 0 ]; then
    echo " 🚨 파일 전송에 실패했습니다. 스크립트를 중단합니다."
    exit 1
fi
echo " ✅ 파일 전송이 완료되었습니다."

# 4. EC2에 접속하여 기존 애플리케이션 종료 및 재시작
echo " (4/5) EC2에 접속하여 애플리케이션을 재시작합니다..."
ssh -i "$PEM_KEY" "$EC2_USER@$EC2_HOST" "
    set -e

    # --- (A) 애플리케이션 포트 설정 (★ 중요 ★) ---
    YOUR_APP_PORT=8080 # 본인의 Spring Boot 포트로 변경하세요.

    JAR_PATH=\"$REMOTE_APP_DIR/$JAR_NAME\"
    LOG_PATH=\"$REMOTE_APP_DIR/app.log\"

    # --- (B) 'fuser'를 사용하여 포트 기준 프로세스 종료 ---
    echo \">> $YOUR_APP_PORT 포트를 사용하는 기존 프로세스를 종료합니다.\"

    # -k: 해당 포트를 사용하는 프로세스에 SIGKILL(9) 전송
    # -n tcp: TCP 프로토콜 지정
    # '|| true': fuser가 종료할 프로세스를 찾지 못해 0이 아닌 값을 반환해도
    #           set -e 로 인해 스크립트가 중단되지 않도록 함.
    fuser -k -n tcp \$YOUR_APP_PORT || true

    # SIGKILL(9)는 즉각적이지만, OS가 포트를 정리할 시간을 주기 위해 잠시 대기
    sleep 2

    # --- (C) 새 애플리케이션 실행 ---
    echo \">> 새 애플리케이션을 \$JAR_PATH 경로에서 시작합니다.\"
    nohup java -jar \$JAR_PATH > \$LOG_PATH 2>&1 &
"
if [ $? -ne 0 ]; then
    echo " 🚨 EC2에서 스크립트 실행에 실패했습니다."
    exit 1
fi

# 5. 배포 완료
echo " (5/5) 모든 배포 과정이 완료되었습니다. 🎉"
echo " EC2 인스턴스(${EC2_HOST})에서 애플리케이션이 실행 중입니다."
echo " 로그 확인: ssh -i $PEM_KEY $EC2_USER@$EC2_HOST tail -f $REMOTE_APP_DIR/app.log"