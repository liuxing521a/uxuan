package com.uxuan.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class Objects {
	
    private static final String NULL = "null";
    private static final String EMPTY_STR = "";

    /** 定义toString时忽略的属性名称字符串集合 */
    private static final String[] IGNORE_FIELD_NAMES =  {
    	"log", "logger",  "serialVersionUID"
    };

    /** 定义toString时忽略的属性类名字符串集合 */
    private static final String[] IGNORE_FIELD_CLASSES =  {
        "org.apache.log4j.Logger",
        "java.util.logging.Logger", 
        "org.slf4j.Logger", 
        "java.lang.Object", 
        "java.lang.Class",
        "java.lang.reflect.AccessibleObject", 
        "java.lang.reflect.Field",
        "java.lang.reflect.Method", 
        "java.lang.reflect.Constructor"
    };

    // =====================================================
    // isNull(); nonNull();
    // isEmpty(); nonEmpty();
    // =====================================================
    /**
     * 判断指定的对象是否为null。
     * 
     * @param obj 要判断的对象实例
     * @return true：为null false：非null
     */
    public static boolean isNull(Object obj)
    {
        return obj == null;
    }

    /**
     * 判断指定的对象是否不为null。
     * 
     * @param obj 要判断的对象实例
     * @return true：非null false：为null
     */
    public static boolean nonNull(Object obj)
    {
        return !isNull(obj);
    }

    /**
     * 判断指定的对象数组是否为空。
     * 
     * @param objs 要判断的对象数组实例
     * @return true：对象数组为空 false：对象数组非空
     */
    public static boolean isEmpty(Object[] objs)
    {
        return isNull(objs) || objs.length == 0;
    }

    /**
     * 判断指定的对象数组是否非空。
     * 
     * @param objs 要判断的对象数组实例
     * @return true：对象数组非空 false：对象数组为空
     */
    public static boolean nonEmpty(Object[] objs)
    {
        return !isEmpty(objs);
    }

    /**
     * 判断指定的集合类是否为空。<BR>
     * 为空的含义是指：该集合为null，或者该集合不包含任何元素。
     * 
     * @param coll 要判断的集合实例
     * @return true：集合为空 false：集合非空
     */
    public static boolean isEmpty(Collection<?> coll)
    {
        return isNull(coll) || coll.isEmpty();
    }

    /**
     * 判断指定的集合类是否非空。 <BR>
     * 非空的含义是指：该集合非null并且该集合包含元素。
     * 
     * @param coll 要判断的集合实例
     * @return true：集合非空 false：集合为空
     */
    public static boolean nonEmpty(Collection<?> coll)
    {
        return !isEmpty(coll);
    }

    /**
     * 判断指定的Map类是否为空。 <BR>
     * 为空的含义是指：该Map为null，或者该Map不包含任何元素。
     * 
     * @param map 要判断的Map实例
     * @return true：Map为空 false：Map非空
     */
    public static boolean isEmpty(Map<?, ?> map)
    {
        return isNull(map) || map.isEmpty();
    }

    /**
     * 判断指定的Map类是否非空。<BR>
     * 非空的含义是指：该Map非null并且该Map包含元素。
     * 
     * @param map 要判断的Map实例
     * @return true：Map非空 false：Map为空
     */
    public static boolean nonEmpty(Map<?, ?> map)
    {
        return !isEmpty(map);
    }

    /**
     * 判断指定的CharSequence类是否为空。 <BR>
     * 为空的含义是指：该CharSequence为null，或者该CharSequence长度为0。<BR>
     * 对String类型，虽然也属于CharSequence，<BR>
     * 但因为通常字符串都有特殊的处理要求，所以使用方法isEmpty(String str);
     * 
     * @param charSeq 要判断的CharSequence实例
     * @return true：CharSequence为空 false：CharSequence非空
     */
    public static boolean isEmpty(CharSequence charSeq)
    {
        return isNull(charSeq) || charSeq.length() == 0;
    }

    /**
     * 判断指定的CharSequence类是否非空。 <BR>
     * 非空的含义是指：该CharSequence非null并且该CharSequence包含元素。<BR>
     * 对String类型，虽然也属于CharSequence， <BR>
     * 但因为通常字符串都有特殊的处理要求，所以使用方法nonEmpty(String str);
     * 
     * @param charSeq 要判断的CharSequence实例
     * @return true：CharSequence非空 false：CharSequence为空
     */
    public static boolean nonEmpty(CharSequence charSeq)
    {
        return !isEmpty(charSeq);
    }

    /**
     * 判断指定的String类是否为空。 <BR>
     * 为空的含义是指：该String为null，或者该String为空串""。
     * 
     * @param str 要判断的String实例
     * @return true：String为空 false：String非空
     */
    public static boolean isEmpty(String str)
    {
        return isNull(str) || EMPTY_STR.equals(str);
    }

    /**
     * 判断指定的String类是否非空。 <BR>
     * 非空的含义是指：该String非null并且该String不为空串。
     * 
     * @param str 要判断的String实例
     * @return true：String非空 false：String为空
     */
    public static boolean nonEmpty(String str)
    {
        return !isEmpty(str);
    }

    /**
     * 判断指定的String类是否为空。<BR>
     * 为空的含义是指：该String为null，或者该String为空串""。
     * 
     * @param str 要判断的String实例
     * @return true：String为空 false：String非空
     */
    public static boolean isTrimEmpty(String str)
    {
        return isNull(str) || EMPTY_STR.equals(str.trim());
    }

    /**
     * 判断指定的String类是否非空。 <BR>
     * 非空的含义是指：该String非null并且该String不为空串。
     * 
     * @param str 要判断的String实例
     * @return true：String非空 false：String为空
     */
    public static boolean isTrimNotEmpty(String str)
    {
        return !isTrimEmpty(str);
    }

    // =============================================================
    // toString()
    // 1. Java 基本型别，包括基本对象型别 直接调用对象本身的toString()
    // 2. 简单的Bean类型 采用反射机制取得每个属性的值
    // 3. Java集合类型 如：Collection，Map以及对象数组
    // 4. 非Java基本型别的对象类型作为属性
    // 5. enum处理：能正确调用object.toString()，归类为Java基本类型
    // 6. 继承关系处理：先取得所有父类，再取得所有属性 递归toString
    // 7. 内部类处理：内部类包含了一个对外部类的默认引用，其名称会包含"this$",忽略即可
    // =============================================================

    
    public static <T> void addAll(List<T> list, T[] array) {
        if (isNull(list) || isNull(array)) {
            return;
        }

        for (T t : array) {
            list.add(t);
        }
    }

    /**
     * 判断属性字段是否为toString时忽略的字段属性。
     *
     * @param field 需要判断是否忽略属性的字段。
     * @return true：为忽略属性 false：非忽略属性
     */
    public static boolean isIgnoreField(Field field)
    {
        if (isNull(field))
        {
            return false;
        }

        // 根据名称判断总归比根据类名判断要快
        return isIgnoreFieldByName(field) || isIgnoreFieldByClass(field);
    }

    /**
     * 根据字段的Name判断是否该字段为已定义的忽略属性
     *
     * @param field 需要判断是否忽略属性的字段。
     * @return true：为忽略属性 false：非忽略属性
     */
    private static boolean isIgnoreFieldByName(Field field)
    {
        // 如果是已定义的需要忽略的属性
        for (String fieldName : IGNORE_FIELD_NAMES)
        {
            if (fieldName.equals(field.getName()))
            {
                return true;
            }
        }

        // 如果是需要模糊匹配需要忽略的属性
        // 说明：如果一个类定义了内部类，内部类保留一个对外部类的默认引用
        // 这会导致递归toString时堆栈溢出，而这个默认引用又不需要toString，所以忽略
        if (field.getName().indexOf("this$") != -1)
        {
            return true;
        }

        return false;
    }

    /**
     * 根据字段的Class判断是否该字段为已定义的忽略属性
     *
     * @param field 需要判断是否忽略属性的字段。
     * @return true：为忽略属性 false：非忽略属性
     */
    private static boolean isIgnoreFieldByClass(Field field)
    {
        Class<?> clazz = null;
        for (String className : IGNORE_FIELD_CLASSES)
        {
        	clazz = ClassUtil.forName(className);
            if (clazz != null && clazz.isAssignableFrom(field.getType()))
            {
                return true;
            }
        }

        return false;
    }
    
    /**
     * 通用的toString方法。 <BR>
     * 1. 以对象形式存在的基本型别数组，如：byte[],long[]需要特殊处理。<BR>
     * @param obj 要转化为字符串的对象
     * @return 转化后的字符串
     */
    public static String toString(Object obj) {
        if (isNull(obj)) {
            return NULL;
        }

        Class<?> clazz = obj.getClass();
        if (clazz.equals(Object.class) || clazz.equals(Class.class)) {
            return obj.toString();
        }

        // 如果是Java基本型别 可以直接调用toString得到正确的信息
        if (ClassUtil.isJavaBasicType(clazz)) {
            return obj.toString();
        }

        StringBuilder sb = new StringBuilder();

        // 如果是数组类
        if (clazz.isArray()) {
            // Java基本型别的数组，可以利用Arrays.toString()完成转化，
            // 这里转为对象数组再用deepToString()，只是为了添加附加信息和减少代码
            if (ClassUtil.isPrimitiveArray(clazz)) {
                Object[] objs = new Object[] {obj};
                sb.append(clazz.getSimpleName()).append("=");
                String objsStr = Arrays.deepToString(objs);
                sb.append(objsStr.substring(1, objsStr.length() - 1));
                return sb.toString();
            }

            // 基本类型的数组已经处理，这里只可能是对象数组。
            // 将基本型别数组与对象数字分开处理的原因是：
            // 基本型别数组可以利用Arrays.deepToString(Object[] objs)转为String
            // 但对象数组中的对象没有声明toSring 方法时也需要deepToString，
            // Arrays.deepToString方法就无能为力了
            return toString((Object[])obj);
        }

        // 如果是集合类Collection
        if (Collection.class.isAssignableFrom(clazz)) {
            return toString((Collection<?>)obj);
        }

        // 如果是映射类Map
        if (Map.class.isAssignableFrom(clazz)) {
            return toString((Map<?, ?>) obj);
        }

        // 到这里，是Bean类型的对象，反射每个字段toString
        sb.append(clazz.getSimpleName()).append("{");
        List<Field> fields = ClassUtil.getAllField(clazz);

        if (fields.size() > 0)
        {
            boolean isAppend = false;
            for (Field field : fields) {
                // 需要忽略的属性
                if (isIgnoreField(field)) {
                    continue;
                }
                
                field.setAccessible(true);
                
                isAppend = true;
                sb.append(field.getName()).append("=");
                try {
                    // 这里如果对每一个属性都调用ObjectUtil.toString()
                    // 对内部类，会造成堆栈溢出，需要解决
                    sb.append(toString(field.get(obj))).append(",");
                } catch (Exception e) {
                    sb.append("???,");
                }
            }
            
            if (isAppend) {
                sb.deleteCharAt(sb.length() - 1);
            }
        }
        sb.append("}");
        return sb.toString();
    }


    /**
     * 将对象数组转换为String。
     * @param objs 对象数组
     * @return 转换后的字符串
     */
    public static String toString(Object[] objs) {
        if (isNull(objs)) {
            return NULL;
        }
        
        if (objs.length == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(objs.getClass().getSimpleName());
        sb.append(String.format("(length=%s)={", objs.length));
        
        for (Object obj : objs) {
            sb.append(toString(obj)).append(',');
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(']');

        return sb.toString();
    }

    /**
     * 将Collection转换为String
     * @param coll Collection
     * @return 转换后的字符串
     */
    public static String toString(Collection<?> coll) {
        if (isNull(coll)) {
            return NULL;
        }
        
        if (coll.isEmpty()) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(coll.getClass().getSimpleName());
    	sb.append(String.format("(size=%s)={", coll.size()));
        
    	for (Object o : coll) {
    		sb.append(toString(o)).append(',');
		}

        sb.deleteCharAt(sb.length() - 1);
        sb.append('}');

        return sb.toString();
    }

    /**
     * 将Map转换为String
     * @param map Map
     * @return 转换后的字符串
     */
    public static String toString(Map<?, ?> map) {
        if (isNull(map)) {
            return NULL;
        }
        
        if (map.isEmpty()) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(map.getClass().getSimpleName());
        sb.append(String.format("(size=%s)={", map.size()));

        for (Entry<?, ?> entry : map.entrySet()) {
        	sb.append(toString(entry.getKey())).append('=');
        	sb.append(toString(entry.getValue())).append(',');
		}
        
        sb.deleteCharAt(sb.length() - 1);
        sb.append('}');

        return sb.toString();
    }


    /**
     * 通用的equals 方法
     * @param a 对象a
     * @param b 对象b
     * @return true 两个对象值相同 false 两个对象值不相同
     */
    public static boolean equals(Object a, Object b) {
    	 return a == b || (nonNull(a) && a.equals(b));
    }
    
    public static int hashCode(Object... objects) {
    	return Arrays.hashCode(objects);
    }
}
