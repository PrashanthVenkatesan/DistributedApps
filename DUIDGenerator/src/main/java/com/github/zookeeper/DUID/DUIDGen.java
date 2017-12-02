package com.github.zookeeper.DUID;

import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;

public class DUIDGen {
	private static ZookeeperConnection conn;
	private static ZooKeeper zk;
	private static String path = "/DUID";
	private static ZMap<Integer, Boolean> rangeMap;
	private static int start = 1;
	private static int buffer = 10000;
	private static int limit = 100000;
	private static int idrange = 100000;
	static {
		try {
			conn = new ZookeeperConnection();
			zk = conn.connect("localhost");
			rangeMap = new ZMap<Integer, Boolean>(Integer.class, Boolean.class, zk, path, CreateMode.EPHEMERAL, true);
			generateRange(start, buffer, limit);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws InterruptedException {
		try {
			AtomicLong number = null;
			while (true) {
				number = getUnusedRange();
				if (number == null) {
					generateRange((start += idrange), buffer, (limit += idrange));
					number = getUnusedRange();
				}

				System.out.println("Got new range :" + number + "-" + (number.get() + buffer - 1));

				assert number != null : "Empty range";

				long n = number.get();
				long limit = n + buffer - 1;
				while (n < limit) {
					n = number.getAndIncrement();
					System.err.println(n);
				}
				Thread.sleep(1000);
			}
		} finally {
			conn.close();
			zk.close();
		}
	}

	private static AtomicLong getUnusedRange() throws InterruptedException {
		AtomicLong number = null;
		for (Entry<Integer, Boolean> e : rangeMap.entrySet()) {
			if (!e.getValue()) {
				number = new AtomicLong(e.getKey());
				rangeMap.put(e.getKey(), true);

				Thread.sleep(2000);
				break;
			}
		}
		return number;
	}

	private static void generateRange(int start, int buffer, int limit) throws InterruptedException {
		rangeMap.clear();
		while (start < limit) {
			rangeMap.put(start, false);
			start += buffer;
		}
		Thread.sleep(2000);
	}
}
