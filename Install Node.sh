# Format and mount /dev/sdc
mkfs.xfs /dev/sdc
mkdir /var/lib/docker
mount /dev/sdc /var/lib/docker

# Disable swap and mount drive permanently
sed -i '/swap/s/^/#/' /etc/fstab
grep -qF '/var/lib/docker' /etc/fstab || echo '/dev/sdc /var/lib/docker xfs defaults 0 0' >> /etc/fstab

# Restart
shutdown -r now

# Disable SELinux
setenforce 0
sed -i --follow-symlinks 's/SELINUX=enforcing/SELINUX=disabked/g' /etc/sysconfig/selinux

# Open Firewall Ports for worker nodes
firewall-cmd --permanent --add-port=10250/tcp
firewall-cmd --permanent --add-port=10255/tcp
firewall-cmd --permanent --add-port=8472/udp
firewall-cmd --permanent --add-port=30000-32767/tcp
firewall-cmd --add-masquerade --permanent
systemctl restart firewalld

# Open additional firewall ports for master
firewall-cmd --permanent --add-port=6443/tcp
firewall-cmd --permanent --add-port=2379-2380/tcp
firewall-cmd --permanent --add-port=10250/tcp
firewall-cmd --permanent --add-port=10251/tcp
firewall-cmd --permanent --add-port=10252/tcp
firewall-cmd --permanent --add-port=10255/tcp
firewall-cmd --permanent --add-port=8472/udp
firewall-cmd --permanent --add-port=30000-32767/tcp
firewall-cmd --add-masquerade --permanent
firewall-cmd --reload
systemctl restart firewalld

# Enable br_netfilter
modprobe br_netfilter
echo '1' > /proc/sys/net/bridge/bridge-nf-call-iptables

# Install and enable Docker
yum install docker -y
systemctl restart docker
systemctl enable docker

# Create local software installation directories
mkdir /var/lib/docker/software
chown balzd00 /var/lib/docker/software

# Copy the Kubernetes install images to /var/lib/docker/software (manually)

# Install Kubernetes
cd /var/lib/docker/software
yum install *.rpm -y
systemctl restart kubelet
systemctl enable kubelet
rm -rf /var/lib/docker/software/*

# Copy the Kubernetes docker images to /var/lib/docker/software (manually)

# Load the Kubernetes docker images
cd /var/lib/docker/software
ls -1 * | xargs --no-run-if-empty -L 1 docker load -i
rm -rf /var/lib/docker/software/*

# Run the following on the kubernetes master node to get a token
kubeadm token create --print-join-command

# Now run the command on the worker node
# kubeadm join 10.233.184.206:6443 --token t4qarh.ubnbsmuurx67v270     --discovery-token-ca-cert-hash sha256:e5b0445c3439e4de3c203c47fa50a59b483efe0b4e677d8cd2c07359454a2550


