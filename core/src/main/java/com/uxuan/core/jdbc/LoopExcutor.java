//package com.uxuan.core.jdbc;
//
//
//import com.uxuan.core.Builder;
//import com.uxuan.core.Syner;
//import com.uxuan.core.Service.OnShutdown;
//import com.uxuan.core.Service.OnStartUP;
//
///**
// * <p>环写数据库线程</p>
// * 
// * @author liuzhen<liuxing521a@163.com>
// * @date 2014-3-17
// */
//final class LoopExcutor extends Syner implements OnShutdown, OnStartUP {
//
//	private LoopExcutor(long interval) {
//		super(interval);
//		isFlag = false;
//		worker = new Thread(this, "database-syner-thread");
//	}
//
//	/** 线程运行标记 */
//	private boolean isFlag;
//
//	/** 执行线程 */
//	private final Thread worker;
//
//	@Override
//	public void onStartUP() {
//		if (worker.isAlive()) {
//			throw new ItasException("thread is alive");
//		}
//
//		isFlag = false;
//		worker.start();
//	}
//
//	@Override
//	public void onShutdown() {
//		isFlag = true;
//	}
//
//	@Override
//	public synchronized void run() {
//		while (!isFlag) {
//			try {
//				worker.wait(super.synDatabase());
//			} catch (InterruptedException exception) {
//				// do nothing
//			} catch (Throwable e) {
//				Logger.error("", e);
//			}
//		}
//	}
//
//
//}
