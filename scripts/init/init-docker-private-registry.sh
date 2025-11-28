# 5000 포트 개방 및 프라이빗 레지스트리 컨테이너 실행
## 외부에서 private registry에 접근하기 위해서는 5000번 포트가 열려 있어야 합니다.
sudo docker run -d -p 5000:5000 --name registry registry:2