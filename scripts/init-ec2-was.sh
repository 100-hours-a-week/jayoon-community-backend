#!/bin/bash
# (이 파일은 EC2에서 'sudo'로 실행될 것입니다)

# --- 스크립트 오류 시 즉시 중단 ---
set -e

echo "========================================"
echo ">> EC2(Ubuntu) 초기 설정을 시작합니다."
echo "   (OpenJDK 21, MySQL Client)"
echo "========================================"

# --- 1. 패키지 매니저 업데이트 ---
echo ">> (1/4) apt-get 업데이트..."
sudo apt-get update -y

# --- 2. OpenJDK 21 설치 (BE용) ---
echo ">> (2/4) OpenJDK 21 설치..."
sudo apt-get install -y openjdk-21-jdk

# --- 4. MySQL Client 설치 (RDS 접속 테스트용) ---
# (Spring 앱이 아닌, 관리자가 터미널에서 접속 테스트를 하기 위해 설치합니다)
echo ">> (4/4) MySQL Client 설치..."
sudo apt-get install -y mysql-client

echo "========================================"
echo ">> ✅ 모든 설치가 완료되었습니다."
echo "========================================"

# --- 버전 확인 ---
echo "--- 설치된 버전 ---"
java -version