# 관리자 권한으로 변경
sudo su

# Portainer 볼륨 생성
docker volume create portainer_data

# Portainer 컨테이너 실행
## 포트 9000은 웹 UI용.
## sock은 docker 엔진을 portainer에서 관리할 수 있도록 연결.
## 기존에 만든 볼륨을 container의 저장소와 마운트합니다.
docker run -d -p 8000:8000 -p 9000:9000 --name=portainer \
    --restart=always \
    -v /var/run/docker.sock:/var/run/docker.sock \
    -v portainer_data:/data \
    portainer/portainer-ce

