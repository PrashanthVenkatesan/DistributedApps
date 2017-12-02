package com.github.zookeeper.DUID;

import java.util.Random;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;

public class zkTest {
	private static ZookeeperConnection conn;
	private static ZooKeeper zk;

	public static void main(String[] args) {
		String path = "/CollectionNode";

		try {
			conn = new ZookeeperConnection();
			zk = conn.connect("localhost");
			/*
			 * Dynamic class map
			 */
			ZMap<String, String> zm = new ZMap<String, String>(String.class, String.class, zk, path,
					CreateMode.EPHEMERAL, false);
			Random rand = new Random();
			int j = 1000;
			while (j-- > 0) {
				String key = "key" + rand.nextInt(10);
				String value = "value" + rand.nextInt(10);
				System.out.println(key + "------" + value);
				zm.put(key, value);
				Thread.sleep(1500);
			}

			/*
			 * non string key ZMap<Integer, String> zm = new ZMap<Integer,
			 * String>(Integer.class, String.class, zk, path, CreateMode.EPHEMERAL, false); Random
			 * rand = new Random(); int j = 1000; while (j-- > 0) { Integer key =
			 * rand.nextInt(10); String value = "value" + rand.nextInt(10);
			 * System.out.println(key + "------" + value); zm.put(key, value);
			 * Thread.sleep(1500); }
			 */

			/*
			 * Integer ,Boolean map
			 * 
			 * ZMap<Integer, Boolean> zm = new ZMap<Integer, Boolean>(Integer.class,
			 * Boolean.class, zk, path, CreateMode.EPHEMERAL, false); Random rand = new Random();
			 * int j = 1000; while (j-- > 0) { Integer key = rand.nextInt(10); Boolean value
			 * = rand.nextBoolean(); System.out.println(key + "------" + value); zm.put(key,
			 * value); Thread.sleep(1500); }
			 */

			/*
			 * Boolean - Range map
			 * 
			 * ZMap<Boolean, Range> zm = new ZMap<Boolean, Range>(Boolean.class,
			 * Range.class, zk, path, CreateMode.EPHEMERAL, false); Random rand = new Random(); int
			 * j = 1000; while (j-- > 0) { Boolean key = rand.nextBoolean(); Range value =
			 * new Range(rand.nextInt(10), rand.nextInt(10)); System.out.println(key +
			 * "------" + value); zm.put(key, value); Thread.sleep(1500); }
			 */

			/*
			 * Failed test case : Currently not supporting custom key class
			 * 
			 * ZMap<Range, Boolean> zm = new ZMap<Range, Boolean>(Range.class,
			 * Boolean.class, zk, path, CreateMode.EPHEMERAL, false); Random rand = new Random();
			 * int j = 1000; while (j-- > 0) { Range key = new Range(rand.nextInt(10),
			 * rand.nextInt(10)); Boolean value = rand.nextBoolean(); System.out.println(key
			 * + "------" + value); zm.put(key, value); Thread.sleep(2500); }
			 */

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
}
