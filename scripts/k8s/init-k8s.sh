apt update -y && apt upgrade -y

# 스왑 비활성화
swapoff -a
sed -i '/ swap / s/^/#/' /etc/fstab

# 커널 파라미터 설정
modprobe br_netfilter

cat <<EOF | tee /etc/sysctl.d/k8s.conf
net.bridge.bridge-nf-call-iptables = 1
net.ipv4.ip_forward = 1
net.bridge.bridge-nf-call-ip6tables = 1
EOF

sysctl --system

# 커널 파라미터 설정을 재부팅 시에도 적용(이건 교재에도 없음)
cat << EOF | tee /etc/modules-load.d/k8s.conf
br_netfilter
EOF

sysctl --system

# containerd 설치
apt update -y
apt install -y containerd

# containerd 기본 파일 생성
mkdir -p /etc/containerd
containerd config default | tee /etc/containerd/config.toml

# cgroup 드라이버 설정 변경
sed -i 's/SystemdCgroup = false/SystemdCgroup = true/' /etc/containerd/config.toml

# containerd 재시작 및 자동 시작 설정
systemctl restart containerd
systemctl enable containerd

# 필수 패키지 설치
apt update -y
apt install -y apt-transport-https ca-certificates curl

# GPG 키 등록, 통신 시 데이터 무결성을 검증. SSL과 비슷
mkdir -p /etc/apt/keyrings

curl -fsSL https://pkgs.k8s.io/core:/stable:/v1.33/deb/Release.key \
  | gpg --dearmor -o /etc/apt/keyrings/kubernetes-apt-keyring.gpg

chmod 644 /etc/apt/keyrings/kubernetes-apt-keyring.gpg

# apt 소스 등록, 이후 update 필요
echo "deb [signed-by=/etc/apt/keyrings/kubernetes-apt-keyring.gpg] \
https://pkgs.k8s.io/core:/stable:/v1.33/deb/ /" \
  | tee /etc/apt/sources.list.d/kubernetes.list

chmod 644 /etc/apt/sources.list.d/kubernetes.list

# 설치 및 버전 업데이트 막기
apt update -y
apt install -y kubelet kubeadm kubectl
apt-mark hold kubelet kubeadm kubectl

# 설치 확인
kubelet --version
kubeadm version
kubectl version