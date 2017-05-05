package com.uxuan.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.Set;

import org.itas.common.Logger;
import org.itas.common.collection.CircularQueue;
import org.itas.core.cache.Cache;

import com.uxuan.core.Base.DataStatus;
import com.uxuan.core.SQLExecutor.Called;
import com.uxuan.core.SQLExecutor.RowMapper;
import com.uxuan.core.Syner.GameBaseSyner;

final class CacheBean implements GameBaseSyner {
	
	private static SQLExecutor excuter;

	static {
		excuter = Ioc.getInstance(SQLExecutor.class);
	}
	
	private final Class<?> clazz;
	private final Base module;
	private final Cache<String, Base> cached;
	private final CircularQueue<Base> insertQueue;
	private final CircularQueue<Base> updateQueue;
	private final CircularQueue<Base> deleteQueue;

	private CacheBean(
			Base module,
			Cache<String, Base> cached,
			CircularQueue<Base> insertQueue,
			CircularQueue<Base> updateQueue,
			CircularQueue<Base> deleteQueue) {
		this.module = module;
		this.clazz = module.getClass();
		this.cached = cached;
		this.insertQueue = insertQueue;
		this.updateQueue = updateQueue;
		this.deleteQueue = deleteQueue;
	}
	
	public Class<?> clazz() {
		return clazz;
	}
	
	public String prefix() {
		return module.prefix();
	}
	
	public Base module() {
		return module;
	}
	
	public boolean contains(String Id) {
		return cached.containsKey(Id);
	}
	
	public Base get(String Id) {
		Base gameBase = cached.get(Id);
		if (Objects.nonNull(gameBase)) {
			return gameBase;
		}
		
		synchronized (Id.intern()) {
			return loadFromDB(Id);
		}
	}
	
	public void cache(Base o) {
		cached.putIfAbsent(o.getId(), o);
	}
	
	public Base remove(String Id) {
		return cached.remove(Id);
	}
	
	@Override
	public void addInsert(Base GameBase) {
		synchronized (insertQueue) {
			insertQueue.push(GameBase);
		}
	}

	@Override
	public void addUpdate(Base GameBase) {
		synchronized (updateQueue) {
			updateQueue.push(GameBase);
		}
	}

	@Override
	public void addDelete(Base GameBase) {
		synchronized (deleteQueue) {
			deleteQueue.push(GameBase);
		}
	}
	
	public Base newInstance(String Id) {
		return module.clone(Id);
	}
	
	private Base loadFromDB(String Id) {
		try {
			Base gameObject = newInstance(Id);
			if (load(gameObject)) {
				cache(gameObject);
			}

			return cached.get(Id);
		} catch (Exception e) {
			Logger.error("", e);
			return null;
		}
	}
	
	private boolean load(Base o) throws Exception {
		Base rs = excuter.queryForObject(
			new RowMapper<Base>() {
				@Override
				public Base mapRow(ResultSet rs) throws SQLException {
					o.doFill(rs);
					return o;
				}
			},

			new Called<PreparedStatement>() {
				@Override 
				public PreparedStatement called(Connection conn) throws Exception {
					PreparedStatement ppst = conn.prepareStatement(o.selectSQL());
					ppst.setString(1, o.getId());
					return ppst;
				}
			});
		
		return rs == o;
	}
	
	void doInsert() throws Exception {
		final int size = insertQueue.size();
		if (size == 0) {
			return;
		}
		
		excuter.executeBatch(
			new Called<Statement>() {
				@Override
				public PreparedStatement called(Connection conn) throws Exception {
					PreparedStatement ppst = conn.prepareStatement(module.insertSQL());
	
					Base data;
					for (int i = 0; i < size; ) {
						synchronized (insertQueue) {
							data = insertQueue.pop();
						}
	
						if (data.getDataStatus() == DataStatus.news) {
							data.doStatement(ppst, DataStatus.news);
							i ++;
						}
	
						if ((i & 0xFF) == 0xFF && ((i + 1) != size)) {
							ppst.executeBatch();// 每255个执行一次,避免一次性太多造成性能下降
						}
					}
	
					return ppst;
				}
		});
	}

	void doUpdate() throws Exception {
		final int size = updateQueue.size();
		if (size == 0) {
			return;
		}
		
		excuter.executeBatch(
			new Called<Statement>() {
				@Override
				public PreparedStatement called(Connection conn) throws Exception {
					PreparedStatement ppst = conn.prepareStatement(module.updateSQL());

					Base data;
					for (int i = 0; i < size; ) {
						synchronized (updateQueue) {
							data = updateQueue.pop();
						}

						if (data.getDataStatus() == DataStatus.modify) {
							data.doStatement(ppst, DataStatus.modify);
							i ++;
						}

						if ((i & 0xFF) == 0xFF && ((i + 1) != size)) {
							ppst.executeBatch();// 每255个执行一次,避免一次性太多造成性能下降
						}
					}

					return ppst;
				}
			});
	}

	void doDelete() throws Exception {
		final int size = deleteQueue.size();
		if (size == 0) {
			return;
		}
		
		excuter.executeBatch(
			new Called<Statement>() {
				@Override
				public PreparedStatement called(Connection conn) throws Exception {
					PreparedStatement ppst = conn.prepareStatement(module.updateSQL());

					Base data;
					for (int i = 0; i < size; ) {
						synchronized (deleteQueue) {
							data = deleteQueue.pop();
						}

						if (data.getDataStatus() == DataStatus.destory) {
							data.doStatement(ppst, DataStatus.destory);
							 i ++;
						}

						if ((i & 0xFF) == 0xFF && ((i + 1) != size)) {
							ppst.executeBatch();// 每255个执行一次,避免一次性太多造成性能下降
						}
					}

					return ppst;
				}
			});
	}
	
	void doCreate(Statement statement) throws SQLException {
		module.doCreate(statement);
	}
	
	void doAlter(Statement statement) throws SQLException {
		Set<String> clumns = excuter.tableColumns(module.tableName());
		module.doAlter(statement, clumns);
	}
	
}
