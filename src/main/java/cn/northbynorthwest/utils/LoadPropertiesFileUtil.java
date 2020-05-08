package cn.northbynorthwest.utils;

import java.io.*;
import java.util.Properties;

/**
 * “Go Further进无止境” <br>
 * 〈读取properties文件〉
 *
 * @author Luoxun
 * @create 2020/5/6
 * @since 1.0.0
 */
public class LoadPropertiesFileUtil {
    private static Properties prop = new Properties();
    static {
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(new File("J:\\workspace\\peppa\\src\\main\\resources\\spider.properties")));
            prop.load(in);
        } catch (FileNotFoundException e) {
            System.out.println("properties文件路径书写有误，请检查！");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static  int getIntValue(String intKey) {
        return Integer.parseInt(prop.getProperty(intKey));
    }

    public static String getStringValue(String stringKey) {
        return prop.getProperty(stringKey);
    }

    public static boolean getBooleanValue(String booleanKey) {
        return Boolean.parseBoolean(prop.getProperty(booleanKey));
    }
}
