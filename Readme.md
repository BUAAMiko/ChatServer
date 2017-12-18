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
将工程内的全部文件和jdbc驱动打包即可(主函数为MainService)
## 通信标准
###### 请求UDP和TCP连接
服务器在2333端口将会持续监听UDP数据包，当接收到数据包内容为TCP_Port或者UDP_Port的时候将会返回一个UDP数据包其中以int形式保存服务器分配的端口号
###### UDP连接
所有的UDP连接的数据包中均为序列化的Map实例，其中Type指明请求的数据内容，具体每种所需的其余数据见下表  
* "SQL_Q"   
  "SQL" 中保存查询的SQL语句 (String)  
  返回一个内容为Map<String,String>的List，为查询结果 (List)
* "Register"  
  "Username"中保存用户的昵称 (String)  
  "Password"中保存用户的密码 (String)  
  返回一个内容为int的Id为用户的登录Id (int)
* "Login"  
  "Id"中保存用户的用户名(非昵称) (int/String)  
  "Password"中保存用户的密码 (String)  
  返回用户昵称或者字符串FALSE (String)  
* "Ask_Message"  
  "Id"中保存用户的用户名(非昵称) (int/String)  
  返回一个内容为Map<String,String>的List,为查询结果 (List)
* "Send_Message"    
  "Date"中保存信息的发送日期和时间格式为YYYY-MM-DD HH:MM:SS (String)  
  "From"中保存发送者的Id(非昵称) (int/String)
  "To"中保存接收者的Id(非昵称) (int/String)
  "MessageType"中保存信息的类型如Text,Picture,File等 (String)
  "Message"中保存信息的具体内容 (String)
  "SubMessage"中保存信息的其余内容如文件名 (String)
  "MessageType"暂定仅为Text,纯字符信息时SubMessage为字符串"NULL"
  返回一个正整数int值则成功 (int)
###### TCP连接
所有的TCP连接的数据包中均为序列化的Map实例，返回为请求内容的序列化内容，其中请求数据包中"Type"指明请求的数据内容，具体每种所需的其余数据见下表  
* "Media"  
  TODO
* "Picture"  
  TODO