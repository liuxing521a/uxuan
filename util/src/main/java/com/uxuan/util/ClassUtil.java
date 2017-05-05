package com.uxuan.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public final class ClassUtil {

    public static boolean isJavaBasicType(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }

        return  clazz.isPrimitive() 
        		|| Number.class.isAssignableFrom(clazz)
                || Character.class.isAssignableFrom(clazz)
                || Boolean.class.isAssignableFrom(clazz)
                || CharSequence.class.isAssignableFrom(clazz)
                || Enum.class.isAssignableFrom(clazz)
                || Date.class.isAssignableFrom(clazz)
                || Calendar.class.isAssignableFrom(clazz);
    }

    /**
     * 判断指定的类是否为Collection（或者其子类或者其子接口）。
     * @param clazz 需要判断的类
     * @return true：是Collection false：非Collection
     */
    public static boolean isCollection(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }

        return Collection.class.isAssignableFrom(clazz);
    }

    /**
     * 判断指定的类是否为Map（或者其子类或者其子接口)。
     * @param clazz 需要判断的类
     * @return true：是Map false：非Map
     */
    public static boolean isMap(Class<?> clazz) {
        if (clazz == null)  {
            return false;
        }

        return Map.class.isAssignableFrom(clazz);
    }

    /**
     * 判断指定的类是否为Java基本型别的数组。
     * @param clazz 需要判断的类
     * @return true：是Java基本型别的数组 false：非Java基本型别的数组
     */
    public static boolean isPrimitiveArray(Class<?> clazz) {
        if (clazz == null) {
        	return false;
        } else if (clazz == byte[].class){
        	return true;
        } else if (clazz == short[].class) {
        	return true;
        } else if (clazz == int[].class) {
        	return true;
        } else if (clazz == long[].class) {
        	return true;
        } else if (clazz == char[].class) {
        	return true;
        } else if (clazz == float[].class) {
        	return true;
        } else if (clazz == double[].class) {
        	return true;
        } else if (clazz == boolean[].class) {
        	return true;
        } else {
        	return false;
        }
    }

    /**
     * 判断指定的类是否为Java基本型别的数组。
     * @param clazz 需要判断的类
     * @return true：是Java基本型别的数组 false：非Java基本型别的数组
     */
    public static boolean isPrimitiveWrapperArray(Class<?> clazz)
    {
        if (clazz == null) {
            return false;
        } else if (clazz == Byte[].class) {
            return true;
	    } else if (clazz == Short[].class) {
	    	return true;
		} else if (clazz == Integer[].class) {
            return true;
		} else if (clazz == Long[].class) {
            return true;
		} else if (clazz == Character[].class) {
            return true;
		} else if (clazz == Float[].class) {
            return true;
		} else if (clazz == Double[].class) {
            return true;
		} else if (clazz == Boolean[].class) {
            return true;
		} else {
            return false;
    	}
    }

    /**
     * 获取包括该类本身但不包含java.lang.Object的所有超类。
     * @param clazz Class
     * @return 超类数组
     */
    public static List<Class<?>> getAllClass(Class<?> clazz) {
        List<Class<?>> clazzList = new ArrayList<Class<?>>();
        getAllSupperClass0(clazzList, clazz);
        
        return clazzList;
    }

    private static void getAllSupperClass0(List<Class<?>> clazzList,  Class<?> clazz) {
        if (clazz.equals(Object.class)) {
            return;
        }
        
        clazzList.add(clazz);
        getAllSupperClass0(clazzList, clazz.getSuperclass());
    }
    
    /**
     * 获取该类所有实现的接口数组。
     * @param clazz Class
     * @return 该类所有实现的接口数组
     */
    public static List<Class<?>> getAllInterface(Class<?> clazz) {
        List<Class<?>> clazzList = new ArrayList<Class<?>>();
        Class<?>[] interfaces = clazz.getInterfaces();

        for (Class<?> interfaceClazz : interfaces) {
            clazzList.add(interfaceClazz);
            getAllSupperInterface0(clazzList, interfaceClazz);
        }

        return clazzList;
    }

    private static void getAllSupperInterface0(List<Class<?>> clazzList, Class<?> clazz) {
        if (clazz.equals(Object.class)) {
            return;
        }

        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> interfaceClazz : interfaces) {
            clazzList.add(interfaceClazz);
            getAllSupperInterface0(clazzList, interfaceClazz);
        }
    }

    /**
     * 获取包括该类本身以及所有超类（不含java.lang.Object）和实现的接口中定义的属性。
     * @param clazz Class
     * @return 属性数组
     */
    public static List<Field> getAllField(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<Field>();
        
        List<Class<?>> supperClazzs = getAllClass(clazz);
        for (Class<?> cls : supperClazzs) {
        	Objects.addAll(fieldList, cls.getDeclaredFields());
		}
        
        List<Class<?>> supperInterfaces = getAllInterface(clazz);
        for (Class<?> cls : supperInterfaces) {
        	Objects.addAll(fieldList, cls.getDeclaredFields());
		}

        return fieldList;
    }
    
    /**
     * child是否继承parent
     * @param child 子类
     * @param parent 父类
     * @return child是parent子类 返回true 否则返回false
     */
    public static boolean isExtends(Class<?> child, Class<?> parent) {
    	return parent.isAssignableFrom(child);
    }
    
    /**
     * 注册类
     * @param className 目标注册类名
     * @return 类class
     */
    public static Class<?> forName(String className) {
    	try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(String.format("class not found exception:[%s]", className), e);
		}
    }
    
    /**
     * 获得泛型的类型
     * @param cls 包含泛型的类
     * @param fileName 泛型引用名
     * @return 泛型类类型 class
     */
    public static Class<?> getGenericsType(Class<?> cls, String fileName) {
    	try {
			Field field1 = cls.getDeclaredField(fileName);
			String type = field1.getGenericType().toString();
			
			int beginPos;
			int endPos;
			if ((beginPos = type.indexOf("<")) > 0 	&& (endPos = type.indexOf(">")) > 0) 
			{
				if (endPos < beginPos + 1) {
					return null;
				}
				
				type = type.substring(beginPos + 1, endPos);
				return forName(type);
			}
			
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
    }
    
    /**
     * 创建对象
     * @param className  类名
     * @return 创建的新对象
     */
    public static <T> T newInstance(String className) throws Exception {
    	return newInstance(className, null, null);
    }

    /**
     * 创建对象
     * @param className  类名
     * @return 创建的新对象
     */
    public static <T> T newInstance(Class<?> cls) throws Exception {
    	return newInstance(cls, null, null);
    }
    
    /**
     * 创建对象
     * @param className  类名
     * @param args 构造函数参数
     * @param parameterTypes 构造函数参数类型
     * @return 创建的新对象
     */
    public static <T> T newInstance(String className, Object[] args, Class<?>[] parameterTypes) throws Exception {
    	Class<?> cls = forName(className);
    	return newInstance(cls, args, parameterTypes);
    }
    
    /**
     * 创建对象
     * @param cls  类class
     * @param args 构造函数参数
     * @param parameterTypes 构造函数参数类型
     * @return 创建的新对象
     * @throws InvocationTargetException 
     * @throws IllegalArgumentException 
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     * @throws SecurityException 
     * @throws NoSuchMethodException 
     */
    @SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<?> cls, Object param, Class<?>[] paraTypes) throws Exception {
		T t;
		if (Objects.isNull(param)) {
		    final Constructor<?> cons = cls.getDeclaredConstructor();
		    final boolean isAccess = cons.isAccessible();

		    cons.setAccessible( true );
		    t = (T)cons.newInstance();
		    cons.setAccessible(isAccess);
		} else {
			
			
			 
		    final Constructor<?> cons = cls.getDeclaredConstructor(paraTypes);
		    final boolean isAccess = cons.isAccessible();

		    cons.setAccessible(true);
		    if (param instanceof int[]) {
		    	t = (T)cons.newInstance((int[])param);
		    } else if (param instanceof long[]) {
		    	t = (T)cons.newInstance((long[])param);
		    } else {
		    	t = (T)cons.newInstance((Object[])param);
		    }
		    cons.setAccessible(isAccess);
		}

		return t;
    }
    
	
    private ClassUtil() { }
}
