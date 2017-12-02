package com.github.zookeeper.DUID;

import java.io.Serializable;

public class Range implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int min;
	private int max;

	public Range(final int min, final int max) {
		this.min = min;
		this.max = max;
	}

	@Override
	public String toString() {
		return "min = " + min + " max = " + max;
	}
}
