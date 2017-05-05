package com.uxuan.util;

import java.lang.reflect.Constructor;

public class LifeQueue<T> {
	
	/** 最大个数*/
	private int maxValue;
	
	/** 最小个数*/
	private int minValue;
	
	/** 已经保存数量*/
	private int existValue;
	
	/** 存储类*/
	private Constructor<T> constructor;
	
	/** 数据存储*/
	private transient Queue<T> data;
	
	public LifeQueue(Class<T> clazz) {
		this(clazz, 64, 256);
	}
			
	public LifeQueue(Class<T> clazz, int minValue, int maxValue) {
		try {
			this.constructor = clazz.getDeclaredConstructor();
			this.constructor.setAccessible(true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.data = new Queue<>();
	}
	
	
	/**
	 * @return the maxValue
	 */
	public int getMaxValue() {
		return maxValue;
	}

	/**
	 * @param maxValue the maxValue to set
	 */
	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	/**
	 * @return the minValue
	 */
	public int getMinValue() {
		return minValue;
	}

	/**
	 * @param minValue the minValue to set
	 */
	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}

	public synchronized T pop() {
		try {
			T value = data.pop();
			if (value != null) {
				return value;
			}
			
			if (existValue <= maxValue) {
				data.push(constructor.newInstance());
				existValue ++;
			}
			
			return (value = data.pop());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public synchronized void push(T value) {
		data.push(value);
		this.notify();
	}
}
