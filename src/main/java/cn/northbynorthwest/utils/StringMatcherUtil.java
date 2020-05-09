package cn.northbynorthwest.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * “Go Further进无止境” <br>
 * 〈字符串匹配工具类〉
 *
 * @author Luoxun
 * @create 2020/5/8
 * @since 1.0.0
 */
public class StringMatcherUtil {

    public static boolean match(String url, String contengRegexUrl) {

        return true;
    }
    public static boolean string2Boolean(String var){
        if ("1".equals(StringUtils.strip(var).trim())){
            return true;
        }
        if ("true".equalsIgnoreCase(StringUtils.strip(var).trim())){
            return true;
        }
        if ("y".equalsIgnoreCase(StringUtils.strip(var).trim())){
            return true;
        }
        return false;
    }
    public static float string2Float(String var){
        return Float.valueOf(StringUtils.strip(var).trim());
    }
    public static int string2Integer(String var){
        return Integer.valueOf(StringUtils.strip(var).trim());
    }
}
