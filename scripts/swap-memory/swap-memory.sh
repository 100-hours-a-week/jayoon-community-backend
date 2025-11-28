# swap 메모리는 기존 RAM 보다 두 배 정도 하면 적당하다고 한다.
sudo fallocate -l 2G /swapfile
# 보안 때문에 루트만 읽고 쓸 수만 있도록 변경
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
# 확인
free -h
# 재부팅 되었을 때도 해당 파일에 메모리 스왑을 적용합니다.
echo "/swapfile none swap sw 0 0" >> /etc/fstab
# 스왑효율은 기본적으로 60인데 너무 자주 일어나면 성능 이슈가 있으므로 10으로 낮춥니다.
echo "vm.swappiness = 10" >> /etc/sysctl.conf