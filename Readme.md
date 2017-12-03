## 聊天软件服务器
基于java的支持TCP和UDP的服务端，支持数据和文件的传输
## 运行环境需求
* JRE 1.8
* Mysql
## 编译环境需求
* JDK 1.8
* Jdbc driver for Mysql
* IntelliJ IDEA
## 编译
将工程内的全部文件和jdbc驱动打包即可（主函数为MainService）
## 通信标准
###### 请求UDP和TCP连接
服务器在2333端口将会持续监听UDP数据包，当接收到数据包内容为TCP_Port或者UDP_Port的时候将会返回一个UDP数据包其中以int形式保存服务器分配的端口号
###### UDP连接
所有的UDP连接的数据包中均为序列化的Map实例，其中Type指明请求的数据内容，具体每种所需的其余数据见下表  
* "SQL_Q"   
  "SQL" 中保存查询的SQL语句（String）  
  返回一个内容为Map<String,String>的List，为查询结果（List）
* "Login"  
  "Id"中保存用户的用户名（非昵称）（int/String）  
  "Password"中保存用户的密码（String）  
  返回用户昵称或者字符串FALSE（String）  
* "Ask_Message"  
  "Id"中保存用户的用户名（非昵称）（int/String）  
  返回一个内容为Map<String,String>的List，为查询结果（List）
* "Send_Message"  
  TODO
###### TCP连接
所有的TCP连接的数据包中均为序列化的Map实例，返回为请求内容的序列化内容，其中请求数据包中"Type"指明请求的数据内容，具体每种所需的其余数据见下表  
* "Media"  
  TODO
* "Picture"  
  TODO