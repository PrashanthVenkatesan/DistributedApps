package com.github.zookeeper.DUID;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class ZMap<K, V> implements Map<K, V>, Synchronizable {

	private SynchronizingWatcher watcher;
	protected final Map<K, V> map;

	protected final Class<? extends K> keyClass;
	protected final Class<? extends V> valueClass;

	private final ZooKeeper zk;
	private final String znode;
	private CreateMode createMode;

	public ZMap(final Class<? extends K> keyClass, final Class<? extends V> valueClass, final ZooKeeper zk,
			final String znode, final CreateMode createMode, final boolean sortedMap)
			throws KeeperException, InterruptedException {
		super();
		if (sortedMap) {
			this.map = new TreeMap<K, V>();
		} else {
			this.map = new HashMap<K, V>();
		}
		this.zk = zk;
		this.keyClass = keyClass;
		this.valueClass = valueClass;
		this.znode = znode;
		this.createMode = createMode;

		createIfAbsent();

		updateZnodeMode();

		this.watcher = new SynchronizingWatcher(this);
		this.synchronize();
	}

	private void updateZnodeMode() {
		if (createMode == CreateMode.PERSISTENT || createMode == CreateMode.EPHEMERAL)
			return;
		else if (createMode == CreateMode.PERSISTENT_SEQUENTIAL)
			this.createMode = CreateMode.PERSISTENT;
		else if (createMode == CreateMode.EPHEMERAL_SEQUENTIAL)
			this.createMode = CreateMode.EPHEMERAL;
		else
			throw new IllegalArgumentException("Invalid Create mode " + createMode.toString());
	}

	private void createIfAbsent() throws KeeperException, InterruptedException {
		try {
			if (this.zk.exists(this.znode, true) == null) {
				this.zk.create(this.znode, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
		} catch (final KeeperException.NodeExistsException e) {
			System.err.println("skipping creation of znode " + this.znode + " as it already exists");
		}
	}

	@SuppressWarnings("unchecked")
	public void synchronize() {
		synchronized (this.map) {
			try {
				for (final String s : this.zk.getChildren(this.znode, this.watcher)) {
					this.map.put((K) Convertor.bytesToObject(s.getBytes(), this.keyClass), (V) Convertor
							.bytesToObject(this.zk.getData(this.znode + '/' + s, this.watcher, null), this.valueClass));
				}
				System.out.println("Watcher: The current status is " + this.map.toString());
			} catch (final Exception e) {
				e.printStackTrace();
				System.err.println("ERROR: " + e.getMessage());
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected V addChildNode(final Object key, final Object value) throws Exception {
		final String path = this.znode + '/' + key;
		final byte[] valueByte = Convertor.objectToBytes(value, this.valueClass);
		try {
			this.zk.create(path, valueByte, ZooDefs.Ids.OPEN_ACL_UNSAFE, this.createMode);
			return null;
		} catch (final KeeperException.NodeExistsException e) {
			try {
				final Stat stat = this.zk.exists(path, true);
				final byte[] oldval = this.zk.getData(path, true, null);

				this.zk.setData(path, valueByte, stat.getVersion());
				return (V) Convertor.bytesToObject(oldval, this.valueClass);
			} catch (final Exception f) {
				e.printStackTrace();
				throw new Exception(f);
			}
		}
	}

	public int size() {
		return 0;
	}

	public boolean isEmpty() {
		return false;
	}

	public boolean containsKey(Object key) {
		return false;
	}

	public boolean containsValue(Object value) {
		return false;
	}

	public V get(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	public V put(K key, V value) {
		synchronized (this.map) {
			try {
				return this.addChildNode(key, value);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public V remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		// TODO Auto-generated method stub

	}

	public void clear() {
		synchronized (this.map) {
			try {
				for (final String s : this.zk.getChildren(this.znode, this.watcher))
					this.zk.delete(this.znode + '/' + s, -1);
				this.map.clear();
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Set<K> keySet() {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<V> values() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Entry<K, V>> entrySet() {
		return this.map.entrySet();
	}

}
