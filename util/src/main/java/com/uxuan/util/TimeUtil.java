package com.uxuan.util;

import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimeUtil {


	private static volatile long _systemTime = System.currentTimeMillis();
	public static final long MILLIS_PER_SECOND = 1000L;
	public static final long MILLIS_PER_MINUTE = 60000L;
	public static final long MILLIS_PER_HOUR = 3600000L;
	public static final long MILLIS_PER_DAY = 86400000L;
	public static final int SECONDS_PER_MINUTE = 60;
	public static final int SECONDS_PER_HOUR = 3600;
	public static final int SECONDS_PER_DAY = 86400;
	public static final int SECONDS_PER_WEEK = 604800;
	private static final long systemTimeTick = Long.parseLong(System.getProperty("systime.time.tick", "200"));

	private static final ScheduledExecutorService systemTimeTickExecutor = Executors.newSingleThreadScheduledExecutor();

	static {
		systemTimeTickExecutor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				TimeUtil.accessTime(System.currentTimeMillis());
			}
		}, systemTimeTick, systemTimeTick, TimeUnit.MILLISECONDS);
	}


	private static void accessTime(final long millis) {
		_systemTime = millis;
	}

	/**
	 * 当前时间[毫秒]
	 * 
	 * <p>200毫秒更新一次
	 * @return
	 */
	public static long timeMillis() {
		return _systemTime;
	}

	/**
	 * 当前时间[秒]
	 * 
	 * <p>200毫秒更新一次
	 * @return
	 */
	public static int timeSec() {
		return (int) (_systemTime / 1000L);
	}
	
	/**
	 * 当前时间戳
	 * 
	 * <p>200毫秒更新一次
	 * @return
	 */
	public static Timestamp  timestamp() {
		return new Timestamp(_systemTime);
	}
	
	/**
	 * 当前日期
	 * 
	 * <p>200毫秒更新一次
	 * @return
	 */
	public static Date  timeDate() {
		return new Date(_systemTime);
	}

	private TimeUtil() {
	}

}
