# #2.配置Docker的yum库

#首先要安装一个yum工具
sudo yum install -y yum-utils device-mapper-persistent-data lvm2


#安装成功后，执行命令，配置Docker的yum源（已更新为阿里云源）：
sudo yum-config-manager --add-repo https://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo

sudo sed -i 's+download.docker.com+mirrors.aliyun.com/docker-ce+' /etc/yum.repos.d/docker-ce.repo


#更新yum，建立缓存
sudo yum makecache fast



# #3、安装Docker

yum install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin



## 启动Docker

systemctl start docker

## 停止Docker
systemctl stop docker

## 重启
systemctl restart docker

## 设置开机自启
systemctl enable docker

## 执行docker ps命令，如果不报错，说明安装启动成功
docker ps



# ##5.配置镜像加速

====================================================
##镜像地址可能会变更，如果失效可以百度找最新的docker镜像。
##配置镜像步骤如下：

失效的话，删除 /etc/docker/daemon.json 文件，然后，重新以下配置

或者进入 文件中 修改

sudo vim /etc/docker/daemon.json

====================================================

## 创建目录
mkdir -p /etc/docker

## 复制内容
tee /etc/docker/daemon.json <<-'EOF'
{
    "registry-mirrors": [
        "http://hub-mirror.c.163.com",
        "https://mirrors.tuna.tsinghua.edu.cn",
        "http://mirrors.sohu.com",
        "https://ustc-edu-cn.mirror.aliyuncs.com",
        "https://ccr.ccs.tencentyun.com",
        "https://docker.m.daocloud.io",
        "https://docker.awsl9527.cn"
    ]
}
EOF

## 重新加载配置
systemctl daemon-reload

## 重启Docker
systemctl restart docker



# 6、利用Docker快速的安装了MySQL

## 1.2.命令解读

利用Docker快速的安装了MySQL，非常的方便，不过我们执行的命令到底是什么意思呢？

```PowerShell
docker run -d \
  --name mysql \
  -p 3306:3306 \
  -e TZ=Asia/Shanghai \
  -e MYSQL_ROOT_PASSWORD=123 \
  mysql
 
```



```
docker run -d \
  --name mysql \
  -p 3306:3306 \
  -e TZ=Asia/Shanghai \
  -e MYSQL_ROOT_PASSWORD=123 \
  mysql:5.7.19
```





> 解读：
>
> - `docker run -d` ：创建并运行一个容器，`-d`则是让容器以后台进程运行
> - `--name mysql ` : 给容器起个名字叫`mysql`，你可以叫别的
> - `-p 3306:3306` : 设置端口映射。 **宿主机端口:容器内端口**
>   - **容器是隔离环境**，外界不可访问。但是可以**将宿主机端口映射容器内到端口**，当访问宿主机指定端口时，就是在访问容器内的端口了。
>   - 容器内端口往往是由容器内的进程决定，例如MySQL进程默认端口是3306，因此容器内端口一定是3306；而宿主机端口则可以任意指定，一般与容器内保持一致。
>   - 格式： `-p 宿主机端口:容器内端口`，示例中就是将宿主机的3306映射到容器内的3306端口
> - `-e TZ=Asia/Shanghai` : 配置容器内进程运行时的一些参数
>   - 格式：`-e KEY=VALUE`，KEY和VALUE都由容器内进程决定
>   - 案例中，TZ=Asia/Shanghai是设置时区；`MYSQL_ROOT_PASSWORD=123`是设置MySQL默认密码
> - `mysql` : 设置**镜像**名称，Docker会根据这个名字搜索并下载镜像
>   - 格式：`REPOSITORY:TAG`，例如`mysql:8.0`，其中`REPOSITORY`可以理解为镜像名，`TAG`是版本号
>   - 在未指定`TAG`的情况下，默认是最新版本，也就是`mysql:latest`



## 我在学习时使用的mysql版本

```
我学习使用的版本 5.7.19

2024/12/18
最新版本为 9.1.0

```







