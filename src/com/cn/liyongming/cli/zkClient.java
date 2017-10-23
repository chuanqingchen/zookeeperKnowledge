package com.cn.liyongming.cli;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

public class zkClient {
	ZooKeeper zk;
	public static String connectString = "192.168.120.3:2181";
	public static int sessionTimeout = 20000;
	//private CountDownLatch connectedSignal = new CountDownLatch(1);  

	public zkClient() {

	}
	/**
	 * 初始化zookeeper连接，同时监听
	 * @throws Exception
	 */
	@Before
	public void init()throws Exception{
		zk =new ZooKeeper(connectString, sessionTimeout, new Watcher(){
			public void process(WatchedEvent event){
				System.out.println(event.getPath()+"---"+event.getType());
				try {
					zk.getChildren("/", true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		});
		System.out.println(zk.toString());
		//connectedSignal.await();  

	}
	/**
	 * create node
	 * @throws Exception
	 */
	@Test
	public void zkCreate() throws Exception{
//		String cr = zk.create("/20171021", "liyongming".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
//		String cr = zk.create("/liyongming03", "liyongming".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
//		String data = zk.getData("/liyongming03", true, null).toString();
		List<String> children = zk.getChildren("/", true);
		for (String data : children) {
			System.out.println(data);
		}
		
	}
	/**
	 * get node data
	 * @throws Exception
	 */
	@Test
	public void zkGetData() throws Exception{
		byte [] data = zk.getData("/liyongming", false, new Stat());
		System.out.println(new String(data,"UTF-8"));
	}
	/**
	 * delete node
	 * @throws Exception
	 */
	@Test
	public void zkDelete() throws Exception{
		//第二个参数的意思是将所有的版本全部删除
		zk.delete("/liyongming03", 0);
	}
	/**
	 * set node data
	 * @throws Exception
	 */
	@Test
	public void zkSetData() throws Exception{
		zk.setData("/liyongming01", "张三丰".getBytes("UTF-8"), -1);
		byte [] data = zk.getData("/liyongming01", false, null);
		System.out.println(new String(data,"UTF-8"));
	}
}
