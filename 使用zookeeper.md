###zookeeper常见报错
* 端口被占用----java.net.BindException:Addredd already in used,这个原因是zoo.cfg配置文件中的2181端口被占用，解决办法：最好不要直接释放这个端口，首先应该检查机器上面哪个进程在利用这个2181端口，如果该进程不是主要进程，则释放该端口，重新启动zookeeper,
也可以将zoo.cfg中的端口改为其他的端口e.g:2080
* 磁盘没有剩余空间----No space left on Device,一旦遇到磁盘没有空间的时候，zookeeper会立即执行Failover策略，从而退出进程
* 无法找到Myid文件
* 集群中其他机器Leader端口没有打开，主要是3888端口的问题

###zookeeper命令
	>bin/zkServer.sh start    启动当前机器的命令
	>bin/zkServer.sh start -server ip:port 启动其他机器的命令
	>create [-s] [-e] path data acl 其中-s或者-e分别指定节点特性：顺序或者临时节点。默认情况下，不添加这2个参数时，创建的是临时节点，create /app1 "123";   ---acl是权限控制。
	>ls path [watch] e.g ls /
	>get path [watch]获取节点的数据内容和属性内容。
	>set path data [version] 更新指定节点的数据内容
	>delete path [version] 删除指定节点，这个命令使用的情况是在一个节点中不存在子节点的时候，可以使用，rmr path [version]
	>>在测试代码中使用 -1来代表某个节点的所有的version data
	
###Java API调用zookeeper
---
######zookeeper作为分布式服务器框架，主要解决分布式数据一致性问题，它提供了简单的分布式原语，并且对多种编程语言提供了API
---
节点类型(createMode):
	
	* 持久类型
		持久persistent
		持久顺序类型persistent_sequential
	* 临时类型
		临时ephemeral
		临时类型ephemeral_sequential
---
节点不支持递归创建，意思是当父节点不存在的情况下，不能创建子节点<br>
节点中的数据只支持字节化的数据（byte[]）类型的数据<br>开发人员需要自己使用序列化工具和反序列化工具，对于简单类型使用`String.geBytes()`来生成字节数组；对于复杂类型，需要使用`Hession` or `Kryo`等专门的序列化工具来进行序列化<br>

###权限问题
客户端在创建节点之前，zk.addAuthInfo("digest","foo:true".getBytes());<br>
`需要进行客户端程序验证？`

###开源客户端
* ZKClient
* Curator   e.g P132-158

这两个客户端都是对原生API的封装，使得CRUD变的简单粗暴。。



###典型应用场景
`数据发布和订阅`--->push（推模式） and pull(拉模式)
``




[链接](http://www.baidu.com)