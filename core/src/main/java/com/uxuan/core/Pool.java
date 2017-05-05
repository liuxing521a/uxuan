package com.uxuan.core;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.itas.common.ItasException;

import com.uxuan.core.Syner.GameBaseSyner;

public class Pool {

	/** 数据池 */
	private static final DataPool dataPool;

	/** 数据池 */
	private static final ResPool resPool;

	static {
		resPool = Ioc.getInstance(ResPool.class);
		dataPool = Ioc.getInstance(DataPool.class);
	}

	public static <T extends Base> T get(String Id) {
		return dataPool.get(Id);
	}

	public static <T extends Base> T get(Class<T> clazz, String Id) {
		return dataPool.get(clazz, Id);
	}

	public static boolean isCached(String Id) {
		return dataPool.isCached(Id);
	}

	public static <T extends Base> boolean isCached(Class<T> clazz,
			String Id) {
		return dataPool.isCached(clazz, Id);
	}

	public static <T extends Base> T remove(String Id) {
		return dataPool.remove(Id);
	}

	public static <T extends Base> T remove(Class<T> clazz, String Id) {
		return dataPool.remove(clazz, Id);
	}

	public static <T extends Base> T newInstance(String Id) {
		return dataPool.newInstance(Id);
	}

	public static <T extends Base> T newInstance(Class<T> clazz, String Id) {
		return dataPool.newInstance(clazz, Id);
	}

	public static <T extends Resource> T getResource(String Rid) {
		return resPool.get(Rid);
	}

	public static <T extends Resource> List<T> getResource(Class<T> clazz) {
		return resPool.get(clazz);
	}

	public interface DBPool {

		Connection getConnection() throws SQLException;

	}

	public interface DataPool {

		GameBaseSyner getSyner(Class<?> clazz);
		
		<T extends Base> void put(T data);

		<T extends Base> T get(String Id);

		<T extends Base> T get(Class<T> clazz, String Id);

		boolean isCached(String Id);

		<T extends Base> boolean isCached(Class<T> clazz, String Id);

		<T extends Base> T remove(String Id);

		<T extends Base> T remove(Class<T> clazz, String Id);

		<T extends Base> T newInstance(String Id);

		<T extends Base> T newInstance(Class<T> clazz, String Id);

	}

	public interface ResPool {

		<T extends Resource> T get(String rid);

		<T extends Resource> List<T> get(Class<T> clazz);

	}

	public interface ConfigPool {

	}

	private Pool() {
		throw new ItasException("can't create instance...");
	}
}
