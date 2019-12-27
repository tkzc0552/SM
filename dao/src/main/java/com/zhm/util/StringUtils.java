package com.zhm.util;

/**
 * Created by 赵红明 on 2019/6/4.
 */

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * String convert to Boolean
 * @author geewit
 * @since  2015-5-18
 */
@SuppressWarnings({"unused"})
public class StringUtils {

    private static final Set<String> trueValues = Sets.newHashSet("true", "on", "yes");
    private static final Set<String> falseValues = Sets.newHashSet("false", "off", "no");

    public static Boolean convert(String source) {
        if(org.apache.commons.lang3.StringUtils.isNotBlank(source)) {
            source = source.trim().toLowerCase();
            if(trueValues.contains(source)) {
                return Boolean.TRUE;
            } else if(falseValues.contains(source)) {
                return Boolean.FALSE;
            }
        }
        return null;
    }

    /**
     * <p>
     * Joins the elements of the provided array into a single String containing the provided list of elements.
     * </p>
     *
     * <p>
     * No delimiter is added before or after the list. Null objects or empty strings within the array are represented
     * by empty strings.
     * </p>
     *
     * <pre>
     * StringUtils.join(null, *)               = null
     * StringUtils.join([], *)                 = ""
     * StringUtils.join([null], *)             = ""
     * StringUtils.join([1, 2, 3], ';')  = "1;2;3"
     * StringUtils.join([1, 2, 3], null) = "123"
     * </pre>
     *
     * @param array
     *            the array of values to join together, may be null
     * @param separator
     *            the separator character to use
     * @return the joined String, {@code null} if null array input
     */
    public static String join(final char[] array, final String separator) {
        if (array == null) {
            return null;
        }
        final int noOfItems = array.length;
        if (noOfItems <= 0) {
            return org.apache.commons.lang3.StringUtils.EMPTY;
        }
        final StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                buf.append(separator);
            }
            buf.append(array[i]);
        }
        return buf.toString();
    }

    public static boolean isNullOrEmpty(String s) {
        return null == s ? true : "".equals(s) ? true : false;
    }

    public static boolean notNullOrEmpty(String s) {
        return !(null == s ? true : "".equals(s) ? true : false);
    }

    /**
     * <p>
     * 拼接字符串
     * </p>
     * <p>
     * Example: splice("aaa", "bbb", "ccc") Result: [aaa, bbb, ccc]
     * </p>
     *
     * @param args
     * @return
     */
    public static String splice(String... args) {
        StringBuilder b = new StringBuilder(256);
        if (args == null) {
            return "[]";
        }
        int iMax = args.length - 1;
        if (iMax == -1) {
            return "[]";
        }
        b.append('[');
        for (int i = 0;; i++) {
            b.append(args[i]);
            if (i == iMax) {
                return b.append(']').toString();
            }
            b.append(", ");
        }
    }

    /**
     * <p>
     * 拼接字符串
     * </p>
     *
     * @param args
     * @return
     */
    public static StringBuilder simpleSplice(String... args) {
        StringBuilder b = new StringBuilder(256);
        for (String s : args) {
            b.append(s).append(", ");
        }
        return b;
    }

    /**
     * <p>
     * 拼接字符串
     * </p>
     *
     * @param args
     * @return
     */
    public static StringBuilder simpleSplice(int size, String... args) {
        StringBuilder b = new StringBuilder(size);
        for (String s : args) {
            b.append(s).append(", ");
        }
        return b;
    }

    /**
     * <p>
     * 拼接字符串
     * </p>
     *
     * @param args
     * @return
     */
    public static StringBuilder simpleSplice(StringBuilder builder, String... args) {
        for (String s : args) {
            builder.append(", ").append(s);
        }
        return builder;
    }

    /**
     * <p>
     * 拼接字符串
     * </p>
     *
     * @param args
     * @return
     */
    public static String spliceKeyValue(String... args) {
        StringBuilder b = new StringBuilder(256);
        if (args == null)
            return "null";
        int iMax = args.length - 1;
        if (iMax == -1)
            return "[]";

        b.append('[');
        for (int i = 0;; i = i + 2) {
            b.append(args[i]).append('=').append(args[i + 1]);
            if ((i + 1) == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }

    /**
     * <p>
     * 拼接字符串
     * </p>
     *
     * @param args
     * @return
     */
    public static String splice(Object... args) {
        StringBuilder b = new StringBuilder(256);
        if (args == null)
            return "null";
        int iMax = args.length - 1;
        if (iMax == -1)
            return "[]";

        b.append('[');
        for (int i = 0;; i++) {
            b.append(String.valueOf(args[i]));
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }

    /**
     * <p>
     * 两个数组的差集
     * </p>
     *
     * @param arr1:不存在重复元素的数组
     * @param arr2:不存在重复元素的数组
     * @return
     */
    public static Map<String, LinkedList<String>> difference(String[] arr1, String[] arr2) {
        LinkedList<String> list1 = new LinkedList<String>();
        LinkedList<String> list2 = new LinkedList<String>();
        for (String str : arr1) {
            list1.add(str);
        }
        for (String str : arr2) {
            if (list1.contains(str)) {// ...相同的
                list1.remove(str);// ...移除相同的
            } else {// ...不同的
                list2.add(str);
            }
        }
        Map<String, LinkedList<String>> map = new HashMap<String, LinkedList<String>>();
        map.put("differenceSet1", list1);
        map.put("differenceSet2", list2);
        return map;
    }

    /**
     * <p>
     * 两个数组的差集
     * </p>
     *
     * @param arr1:不存在重复元素的数组
     * @param arr2:不存在重复元素的数组
     * @return
     */
    public static Map<String, Map<String, String[]>> difference(JSONArray arr1, JSONArray arr2) {
        // ...[{"menuCode":"001001001","opts":["SYS002","SYS003"]},{"menuCode":"001001011","opts":["SYS002"]}]
        Map<String, String[]> map1 = new HashMap<String, String[]>(arr1.size());
        Map<String, String[]> map2 = new HashMap<String, String[]>(arr2.size());

        JSONObject jsonObj = null;
        String menuCode = null;
        String optCode = null;
        String key = null;
        JSONArray opts = null;
        for (Object object : arr1) {
            jsonObj = (JSONObject) object;
            menuCode = jsonObj.getString("menuCode");
            opts = (JSONArray) jsonObj.get("opts");
            for (int i = 0; i < opts.size(); i++) {
                optCode = (String) opts.get(i);
                map1.put(menuCode + optCode, new String[] { menuCode, optCode });
            }
        }
        for (Object object : arr2) {
            jsonObj = (JSONObject) object;
            menuCode = jsonObj.getString("menuCode");
            opts = (JSONArray) jsonObj.get("opts");
            for (int i = 0; i < opts.size(); i++) {
                optCode = (String) opts.get(i);
                key = menuCode + optCode;
                if (null != map1.get(key)) {// ...相同的
                    map1.remove(key);// ...移除相同的
                } else {// ...不同的
                    map2.put(key, new String[] { menuCode, optCode });
                }
            }
        }
        Map<String, Map<String, String[]>> map = new HashMap<String, Map<String, String[]>>();
        map.put("differenceSet1", map1);
        map.put("differenceSet2", map2);
        return map;
    }

    public static String lpad(Integer length, String lpad, String s) {
        String result = lpad + s;
        if (result.length() < length) {
            result = lpad(length, lpad, result);
        }
        return result;
    }

    public static void main(String[] args) {

//		System.out.println(splice("aaa", "bbb", "ccc"));
//		System.out.println(splice("aaa", 123, "ccc"));
//		System.out.println(spliceKeyValue("key1", "value1", "key2", "value2"));

//		System.out.println(splice("aaa", "bbb", "ccc"));

        // ...[{"menuCode":"001001001","opts":["SYS002","SYS003"]},{"menuCode":"001001011","opts":["SYS002"]}]

//		JSONArray arr1 = new JSONArray();
//		JSONArray opts = new JSONArray();
//		JSONObject jsonObj = new JSONObject();
//		jsonObj.put("menuCode", "001001001");
//		jsonObj.put("opts", opts);
//		arr1.add(jsonObj);
//		opts.add("SYS002");
//		opts.add("SYS003");
//
//		JSONArray arr2 = new JSONArray();
//		jsonObj = new JSONObject();
//		opts = new JSONArray();
//		jsonObj.put("menuCode", "001001011");
//		jsonObj.put("opts", opts);
//		arr2.add(jsonObj);
//		opts.add("SYS002");
//
//		jsonObj = new JSONObject();
//		opts = new JSONArray();
//		jsonObj.put("menuCode", "001001001");
//		jsonObj.put("opts", opts);
//		arr2.add(jsonObj);
//		opts.add("SYS002");
//		opts.add("SYS001");
//
//		Map<String, Map<String, String[]>> map = difference(arr1, arr2);
//
//		System.out.println(map.get("differenceSet1"));
//		System.out.println(map.get("differenceSet2"));

        String domain = getSecondLevelDomain("mk.seedeer.com");
        System.out.println(domain);

    }

    /**
     * 获取请求地址二级域名 <br>
     * <br>
     * 域名+端口，例如 <br>
     * sso.seedeer.com:6080 返回的二级域名为 .seedeer.com <br>
     * localhost:6080 返回的二级域名为 localhost <br>
     * reg.m.seedeer.com:6080 返回的二级域名为 .seedeer.com
     *
     * @author miwei
     * @date 2017年12月19日
     * @time 下午1:50:39
     * @param host
     * @return
     */
    public static String getSecondLevelDomain(String host) {
        String secondLevelDomain = null;
        String domain = null;
        int index = host.indexOf(":");
        if (-1 == index) {
            domain = host;
        } else {
            domain = host.substring(0, index);
        }

        index = domain.lastIndexOf('.');
        if (-1 == index) {
            secondLevelDomain = domain;
        } else {
            String subStr = domain.substring(0, index);
            index = subStr.lastIndexOf('.');
            if (-1 == index) {
                secondLevelDomain = domain;
            } else {
                secondLevelDomain = domain.substring(index+1);
            }
        }
        return secondLevelDomain;
    }

    /**
     * 拼接字符串 <br>
     * <br>
     * 此方法的StringBuilder的capacity=128，若超过此长度，可调用此方法的重构方法
     *
     * @author miwei
     * @date 2017年12月19日
     * @time 下午2:11:59
     * @param strings
     * @return
     */
    public static String concat(String... strings) {
        StringBuilder builder = new StringBuilder(128);
        for (String s : strings) {
            builder.append(s);
        }
        return builder.toString();
    }

    /**
     * 拼接字符串
     *
     * @author miwei
     * @date 2017年12月19日
     * @time 下午2:20:05
     * @param capacity
     * @param strings
     * @return
     */

    public static String concat(int capacity, String... strings) {
        StringBuilder builder = new StringBuilder(capacity);
        for (String s : strings) {
            builder.append(s);
        }
        return builder.toString();
    }
}
