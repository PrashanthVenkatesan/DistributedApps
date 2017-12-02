package com.github.zookeeper.DUID;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public class SynchronizingWatcher implements Watcher {
	private final Synchronizable synchronizable;

	public SynchronizingWatcher(final Synchronizable synchronizable) {
		this.synchronizable = synchronizable;
	}

	public void process(WatchedEvent wevent) {
		try {
			this.synchronizable.synchronize();
			return;
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

}
