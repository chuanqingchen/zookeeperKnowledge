package com.cn.liyongming.cli;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Before;
import org.junit.Test;

public class zkClient {
	ZooKeeper zk;
	public static String connectString = "192.168.120.3:2181";
	public static int sessionTimeout = 2000;
	private CountDownLatch connectedSignal = new CountDownLatch(1);  

	public zkClient() {

	}
	@Before
	public void init()throws Exception{
		zk =new ZooKeeper(connectString, sessionTimeout, new Watcher(){
			public void process(WatchedEvent event){
				System.out.println(event.getPath()+"---"+event.getType());
			};
		});
		System.out.println(zk.toString());
		connectedSignal.await();  

	}
	@Test
	public void zkNew() throws Exception{
		zk =new ZooKeeper(connectString, sessionTimeout, new Watcher(){
			public void process(WatchedEvent event){
				System.out.println(event.getPath()+"---"+event.getType());
			};
		});
		
		System.out.println(zk.toString());
	}
	@Test
	public void zkCreate() throws Exception{
//		String cr = zk.create("/20171021", "liyongming".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		String cr = zk.create("/liyongming", "liyongming".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		String data = zk.getData("/liyongming", true, null).toString();
		System.out.println(data);
	}
}
