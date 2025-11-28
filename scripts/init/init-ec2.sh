#!/bin/bash
# (이 파일은 EC2에서 'sudo'로 실행될 것입니다)

# --- 스크립트 오류 시 즉시 중단 ---
set -e

echo "========================================"
echo ">> EC2(Ubuntu) 초기 설정을 시작합니다."
echo "   (OpenJDK 21, Nginx, MySQL 8.0)"
echo "========================================"

# --- 1. 패키지 매니저 업데이트 ---
echo ">> (1/4) apt-get 업데이트..."
sudo apt-get update -y

# --- 2. OpenJDK 21 설치 (BE용) ---
echo ">> (2/4) OpenJDK 21 설치..."
sudo apt-get install -y openjdk-21-jdk

# --- 3. Nginx 설치 (FE 서빙 및 리버스 프록시용) ---
echo ">> (3/4) Nginx 설치..."
sudo apt-get install -y nginx

# --- 4. MySQL Server 설치 (DB용) ---
echo ">> (4/4) MySQL Server 설치..."
export DEBIAN_FRONTEND=noninteractive
sudo apt-get install -y mysql-server

echo "========================================"
echo ">> ✅ 모든 설치가 완료되었습니다."
echo "========================================"

# --- 5. MySQL 자동 설정 (★ 요청하신 내용 적용 ★) ---
echo ">> (5/5) MySQL 자동 설정을 시작합니다..."

# --- 설정할 비밀번호 ---
ROOT_DB_PASSWORD="root0616"
DB_NAME="community" # application.yml에 맞춘 DB 이름

# 1. 'root'@'localhost' 계정 인증 방식 변경 및 비밀번호 설정
# (auth_socket -> mysql_native_password)
echo "   - 'root'@'localhost' 계정 비밀번호 설정"
sudo mysql -e "ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '$ROOT_DB_PASSWORD';"

# 2. 외부 접속 허용 (bind-address 0.0.0.0)
echo "   - /etc/mysql/mysql.conf.d/mysqld.cnf 파일 수정 (bind-address: 0.0.0.0)"
# bind-address 수정
sudo sed -i 's/bind-address\s*=\s*127.0.0.1/bind-address = 0.0.0.0/g' /etc/mysql/mysql.conf.d/mysqld.cnf
# mysqlx-bind-address도 수정 (이미지에 있었음)
sudo sed -i 's/mysqlx-bind-address\s*=\s*127.0.0.1/mysqlx-bind-address = 0.0.0.0/g' /etc/mysql/mysql.conf.d/mysqld.cnf

# 3. MySQL 재시작 (설정 적용)
echo "   - MySQL 서비스 재시작"
sudo systemctl restart mysql

# 4. DB 생성 및 외부 접속용 'root'@'%' 계정 설정
# (이제 'root'@'localhost'의 비밀번호가 설정됐으므로, 해당 비번으로 접속)
echo "   - '$DB_NAME' 데이터베이스 생성"
echo "   - 'root'@'%' (외부 접속용) 계정 생성 및 권한 부여"
mysql -u root -p"$ROOT_DB_PASSWORD" -e "
    CREATE DATABASE IF NOT EXISTS $DB_NAME;
    CREATE USER IF NOT EXISTS 'root'@'%' IDENTIFIED WITH mysql_native_password BY '$ROOT_DB_PASSWORD';
    GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;
    FLUSH PRIVILEGES;
"

echo "========================================"
echo ">> ✅ 모든 설치 및 MySQL 설정이 완료되었습니다."
echo "   - 'root'@'localhost' (내부용) 비밀번호 설정됨"
echo "   - 'root'@'%' (외부용) 생성 및 모든 권한 부여됨"
echo "   - 'bind-address'가 0.0.0.0으로 변경됨 (외부 접속 허용)"
echo "   - '$DB_NAME' 데이터베이스가 생성됨"
echo "========================================"

# --- 버전 확인 ---
echo "--- 설치된 버전 ---"
java -version
nginx -v
mysql --version
