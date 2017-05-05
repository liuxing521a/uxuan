package com.uxuan.util;

import java.util.List;
import java.util.Map;

import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

/**
 * 对象容器
 * 
 * @author liuzhen(liuxing521a@gmail.com)
 * @time 2016年4月26日 下午2:52:08
 */
public final class Ioc {
	
	private static Ioc instance = new Ioc();
	
	private Ioc() {
	}
	
	private Injector ioc;
	
	public static void initIoc(Module...module) {
		instance.ioc = Guice.createInjector(module);
	}
	
	public static void injectMembers(Object o) {
		instance.ioc.injectMembers(o);
	}

	public static Map<Key<?>, Binding<?>> getBindings() {
		return instance.ioc.getBindings();
	}

	public static <T> Binding<T> getBinding(Key<T> key) {
		return instance.ioc.getBinding(key);
	}

	public static <T> List<Binding<T>> findBindingsByType(TypeLiteral<T> type) {
		return instance.ioc.findBindingsByType(type);
	}

	public static <T> Provider<T> getProvider(Key<T> key) {
		return instance.ioc.getProvider(key);
	}

	public static <T> Provider<T> getProvider(Class<T> type) {
		return instance.ioc.getProvider(type);
	}

	public static <T> T getInstance(Key<T> key) {
		return instance.ioc.getInstance(key);
	}

	public static <T> T getInstance(Class<T> type) {
		return instance.ioc.getInstance(type);
	}
}
