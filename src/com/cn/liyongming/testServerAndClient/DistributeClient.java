package com.cn.liyongming.testServerAndClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

/**
 * 从服务器获取数据（查看哪台机器是可以使用的）
 * 客户端在连接服务器的时候，监听服务器端程序
 * 监控的对象，当服务器端/DistributeServer节点下面的数据发生变化的时候，启动监听调用getChildrenData()来得到服务器中的心得资源的列表
 * @author Administrator
 *
 */
public class DistributeClient {
	private static String connectString = "192.168.120.3:2181";
	private static int sessionTimeout = 30000;
	private ZooKeeper zk;
	private volatile List<String>  servlerList;//JVM的东西
	
	public void getConnection() throws Exception{
		zk = new ZooKeeper(connectString, sessionTimeout, new Watcher(){
			public void process(WatchedEvent event) {
				try {
//					zk.getChildren("/DistributeServer/", true);
					//在这里调用是重点...
					getChildren();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	public void getChildren() throws Exception, InterruptedException{
		List<String> list = zk.getChildren("/DistributeServer", true);
		List<String> lam = new ArrayList<String>();
		for (String li : list) {
//			System.out.println(li);
			byte[] by = zk.getData("/DistributeServer"+"/"+li, true, null);
//			System.out.println(new String(by,"UTF-8"));
			lam.add(new String(by,"UTF-8"));
		}
		servlerList = lam;
		System.out.println(servlerList);
	}
	public void cliBusiness() throws Exception{
		System.out.println("客户端执行的业务代码");
		Thread.sleep(Long.MAX_VALUE);
	}
	
	public static void main(String[] args) throws Exception {
		//获取zookeeper的连接
		DistributeClient distributeClient = new DistributeClient();
		distributeClient.getConnection();
		//获取节点的数据
		//监听服务器节点数据的变化
		distributeClient.getChildren();
		//做自己的业务
		distributeClient.cliBusiness();
	}
}
