```bash
# 1. 查看机器磁盘利用率
df -h

# 2.ilogtail查看ilogtail进程端口
ps -ef | grep 

# 3. 查看是否有日志文件已删除，文件句柄没释放(注有输出说明有文件句柄没释放，需要通过重启processId对应的进程，释放空间)
sudo lsof -p 182075 | grep deleted

# 4. 单机重启ilogtail进程释放句柄
sudo /etc/init.d/ilogtaild restart

```