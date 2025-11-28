# root 권한 실행이 필요합니다.
# 패키지 목록 업데이트 필수 패키지 설치
apt update -y
apt install -y ca-certificates curl

# docker 공식 GPG 키 추가, 패키지의 신뢰성 검증을 위함
install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
chmod a+r /etc/apt/keyrings/docker.asc

# docker 저장소 추가. docker 패키지를 설치하기 위해 공식 docker 저장소를 apt 소스에 추가.
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "${UBUNTU_CODENAME:-$VERSION_CODENAME}") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# 패키지 목록 업데이트. 새로 추가한 docker 저장소를 포함.
apt update -y

# docker 엔진 및 관련 패키지 설치
apt install -y docker-ce=5:28.5.1-1~ubuntu.24.04~noble \
docker-ce-cli=5:28.5.1-1~ubuntu.24.04~noble \
containerd.io \
docker-buildx-plugin \
docker-compose-plugin

# docker 서비스 상태 확인
systemctl status docker