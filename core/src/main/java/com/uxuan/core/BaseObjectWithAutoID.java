package com.uxuan.core;

import org.itas.core.util.UUIDGenerator;

/**
 * <p>自动同步数据库基类</p>
 * 与GameBase不同的是，此类不可以自动创建对象；如果手动创建对象，不需要制定Id
 * @author liuzhen<liuxing521a@gmail.com>
 * @createTime 2014年12月15日下午4:30:51
 */
public abstract class BaseObjectWithAutoID extends Base  {
	
	protected BaseObjectWithAutoID(String Id) {
		super(aotoId(Id));
	}
	
	
	static String aotoId(String Id) {
		if (Id == null || Id.length() < 4) {
			return new UUIDGenerator().toString();
		}
		
		return Id;
	}
	
}
