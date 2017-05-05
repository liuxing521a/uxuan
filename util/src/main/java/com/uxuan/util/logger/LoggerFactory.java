/**
 * 
 */
package com.uxuan.util.logger;

import java.io.File;
import java.io.InputStream;

import com.uxuan.util.Objects;


/**
 * 日志产生工厂
 * 
 * @author liuzhen<liuxing521a@163.com>
 * @createTime 2015年5月7日下午5:04:50
 */
public class LoggerFactory  {
	
	/** 日志生成器*/
	private static volatile LoggerFactorySupport LOGGER_FACTORY;
	
	static {
		setLoggerFactory(Thread.currentThread().getContextClassLoader());
	}
	
	private LoggerFactory() {
	}
	
	/**
	 * 设置日志输出器提供者
	 * 
	 * @param loggerFactory 日志输出器提供者
	 */
	public static void setLoggerFactory(LoggerFactorySupport loggerFactory) {
		if (Objects.nonNull(loggerFactory)) {
			Logger logger = loggerFactory.getLogger(LoggerFactory.class);
			logger.info("using logger: " + loggerFactory.getClass().getName());
			LoggerFactory.LOGGER_FACTORY = loggerFactory;
		}
	}

	/**
	 * 获取日志输出器
	 * 
	 * @param key  分类键
	 * @return 日志输出器, 后验条件: 不返回null.
	 */
	public static Logger getLogger(Class<?> key) {
		return new FailsafeLogger(LOGGER_FACTORY.getLogger(key));
	}
	
	/**
	 * 获取日志输出器
	 * 
	 * @param key 分类键
	 * @return 日志输出器, 后验条件: 不返回null.
	 */
	public static Logger getLogger(String key) {
		return new FailsafeLogger(LOGGER_FACTORY.getLogger(key));
	}
	
	/**
	 * 获取日志级别
	 * 
	 * @return 日志级别
	 */
	public static Level getLevel() {
		return LOGGER_FACTORY.getLevel();
	}
	
	/**
	 * 动态设置日志级别
	 * 
	 * @param level 日志级别
	 */
	public static void setLevel(Level level) {
		LOGGER_FACTORY.setLevel(level);
	}
	
	/**
	 * 获取日志文件
	 * 
	 * @return 日志文件
	 */
	public static File getFile() {
		return LOGGER_FACTORY.getFile();
	}
	
	/**
	 * 根据日志文件自动旋转日志<br><br>
	 * 优先级 sf4jlogger > logger4j > jdklogger
	 * 
	 * @param loader
	 */
	private static void setLoggerFactory(ClassLoader loader) {
		String fileName = loadLoggerFile(loader, "logback.xml");
		if (Objects.nonEmpty(fileName)) {
			setLoggerFactory(new Sf4jLoggerFactory());
			return;
		}
		
		fileName = loadLoggerFile(loader, "logback-test.xml");
		if (Objects.nonEmpty(fileName)) {
			setLoggerFactory(new Sf4jLoggerFactory());
			return;
		}
		
		fileName = loadLoggerFile(loader, "log4j.properties");
		if (Objects.nonEmpty(fileName)) {
			setLoggerFactory(new Logger4jFactory());
			return;
		}
		
		fileName = loadLoggerFile(loader, "logging.properties");
		if (Objects.nonEmpty(fileName)) {
			setLoggerFactory(new JdkLoggerFactory());
			return;
		}
		
		throw new RuntimeException("not logger file for logger support");
	}
	
	/**
	 * 加载日志文件
	 * 
	 * @param loader 类加载器
	 * @param fileName 日志文件名
	 * @return 如果日志存在 返回日志文件名，不存在返回空
	 */
	private static String loadLoggerFile(ClassLoader loader, String fileName) {
		try (InputStream input = loader.getResourceAsStream(fileName)) {
			if (Objects.nonNull(input)) {
				return fileName;
			}
			
			return null;
		} catch (Throwable t) {
			System.err.println("Failed to load logging.properties in classpath for jdk logging config, cause: " + t.getMessage());
			return null;
		}
	}
}