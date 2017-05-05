package com.uxuan.util;


import java.io.Serializable;

/**
 * @author liuzhen(liuxing521a@gmail.com)
 * @time 2016年4月26日 上午8:50:40
 */

public class Pair<K, V> implements Serializable {

	private static final long serialVersionUID = 4859995114341080251L;

	public Pair(K key, V value) {
		this.key = key;
		this.value = value;
	}
	
	private K key;

	private V value;

	public K getKey() {
		return key;
	}

	public V getValue() {
		return value;
	}

	@Override
	public String toString() {
		return key + "=" + value;
	}

	@Override
	public int hashCode() {
		return key.hashCode() * 13 + (value == null ? 0 : value.hashCode());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o instanceof Pair) {
			Pair<?, ?> pair = (Pair<?, ?>) o;
			if (key != null ? !key.equals(pair.key) : pair.key != null)
				return false;
			if (value != null ? !value.equals(pair.value) : pair.value != null)
				return false;
			return true;
		}
		
		return false;
	}
}
