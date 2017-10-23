package com.cn.liyongming.testServerAndClient;

import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

/**
 * 模拟服务器注册
 * 同时提供给第三方数据
 * ---特别需要注意的是这个里面应该写一个socketServer来进行服务器的监听
 * @author Administrator
 *
 */
public class DistributeServer {
	private ZooKeeper zk;
	public ZooKeeper getZk() {
		return zk;
	}
	public void setZk(ZooKeeper zk) {
		this.zk = zk;
	}
	private String connectString = "192.168.120.3:2181";
	private int sessionTimeout = 30000;
	
	public void getConnection() throws Exception{
		zk = new ZooKeeper(connectString, sessionTimeout, new Watcher(){
			public void process(WatchedEvent event) {
				System.out.println("event"+event.getType());
			}
			
		});
	}
	public List<String> getChildren() throws Exception, InterruptedException{
		List<String> list = zk.getChildren("/", false);
		System.out.println(list);
		return list;
	}
	
	public void createDistributeNode(String data) throws Exception, KeeperException, InterruptedException{
		//创建唯一的节点
		//创建临时节点
		zk.create("/DistributeServer/"+data, data.getBytes("UTF-8"), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
	}
	public void bussiuess(String args) throws Exception{
		System.out.println("start " +args +"is working....");
		Thread.sleep(Long.MAX_VALUE);
	}
	public static void main(String[] args) throws Exception {
		//获取zookeeper的链接
		DistributeServer distributeServer = new DistributeServer();
		distributeServer.getConnection();
		ZooKeeper zk = distributeServer.getZk();
		System.out.println(zk);
		List<String> list = distributeServer.getChildren();
		//向zookeeper中添加节点数据
		String abc = "20171023-1907";
		distributeServer.createDistributeNode(abc);
		//客户端的业务
		distributeServer.bussiuess(abc);
	}
}