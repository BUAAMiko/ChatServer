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
* "Send_Message"  
  "From"中保存发送者的Id(非昵称) (int/String)  
  "To"中保存接收者的Id(非昵称) (int/String)  
  "Message"中保存信息的具体内容 (String)  
  "PacketIdentify"中保存任意字符串(可为空) (String)  
  返回数据包中"PacketIdentify"中的字符串 (String)
* "UserInfo"
  "Id"中保存需要查询昵称的用户的Id (int/String)
  返回一个字符串为用户的昵称 (String)
###### TCP连接
TCP数据传输分两次传输第一次将目标数据的字节数写入(int转byte[])，第二次将目标数据写入，读取时第一次读取4字节转化为int，然后持续读取对应长度的数据读取结束后即可断开连接，客户端断开连接后，服务端也会相应地断开连接  
  
所有的TCP连接的数据包中均为序列化的Map实例，返回为请求内容的序列化内容，其中请求数据包中"Type"指明请求的数据内容，具体每种所需的其余数据见下表  
* "Ask_Message"  
  "Id"中保存用户的用户名(非昵称) (int/String)  
  返回一个内容为Map<String,String>的List,为查询结果 (List)
* "Send_Picture"  
  "From"中保存发送者的用户名(非昵称) (int/String)  
  "To"中保存接受这的Id(非昵称) (int/String)  
  "File"中保存File类型的实例 (File)  
  "PacketIdentify"中保存任意字符串(可为空) (String)  
  返回数据包中"PacketIdentify"中的字符串 (String)
* "Send_File"  
  除"Type"以外与Send_Picture相同
* "Ask_File"或"Ask_Picture"  
  "Id"中保存索取文件在服务器的Id号 (int/String)  
  返回一个Map,"File"中保存File实例,"FileName"中保存文件名,"Type"中保存文件类型(由传输时的Type决定,如果上传时为Send_File则返回字符串File,Picture同理)
###### Ask_Message中返回的查询结果的格式
* "Id" 为该消息在服务器的Id，用于接收文件
* "Date" 为发送时的时间和日期格式为"yyyy-MM-dd HH:mm:ss"
* "From" 为发送者的Id
* "To" 为接受者的Id
* "MessageType" 为信息的类型如"Text""Picture""File"
* "Message" 为信息的内容如果是非"Text"则请无视
* "SubMessage" 为文件的名称如果是"Text"则请无视
###### 注意
所有的返回值  
String类型请使用new String(data)来获取  
int类型请使用Functions文件中的bytesToInt函数  
其余类型请使用Functions文件中的bytesToObject函数来获取