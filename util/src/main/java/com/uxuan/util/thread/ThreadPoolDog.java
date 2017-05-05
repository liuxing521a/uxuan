package com.uxuan.util.thread;

import com.uxuan.util.logger.Logger;
import com.uxuan.util.logger.LoggerFactory;


/**
 * 线程池看门狗，监视线程
 * @author liuzhen(liuxing521a@gmail.com)
 * @createTime 2012-4-27上午05:44:46
 */
final class ThreadPoolDog implements Runnable 
{
	private static Logger logger = LoggerFactory.getLogger(ThreadPoolDog.class);
	
	ThreadPool p;
	Thread t;
	int interval;
	boolean isFlag;

	ThreadPoolDog(ThreadPool p)
	{
		this.p = p;
		this.interval = ThreadPool.THREADPOOL_WORK_WAIT_TIMEOUT;
		this.start();
	}

	public void start() 
	{
		isFlag = false;
		t = new Thread(this);
		t.setDaemon(p.isDaemon());
		t.setName(p.getName() + "-Monitor");
		t.start();
	}

	public void setInterval(int i) 
	{
		this.interval = i;
	}

	public void run() 
	{
		while (!isFlag) 
		{
			try {
				synchronized (this) 
				{
					this.wait(interval);
				}

				p.releaseFreeThreads();
			} catch (Throwable t) {
				logger.error("Unexpected exception", t);
			}
		}
	}

	public void stop() 
	{
		this.terminate();
	}

	public synchronized void terminate() 
	{
		isFlag = true;
		this.notify();
	}
}