package com.sunline.ykym.util;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
public class PropertUtils {
    /** props */
    private static Properties props = null;

    /** 定义私有map用来存放读取的内容 */
    private static Map<String, String> proMap = new HashMap<String, String>();

    /** 去读properties文件的内容，获取Properties对象 */
    private static void loadProps() {
        props = new Properties();
        InputStream in = null;
        try {
            // 第一种，通过类加载器进行获取properties文件流
            in = PropertUtils.class.getClassLoader().getResourceAsStream("config/config.properties");
            // 第二种，通过类进行获取properties文件流
            // in =
            // PropertyUtil.class.getResourceAsStream("/config.properties");
            props.load(in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 
     * @param key
     *            key
     * @return key
     *
     */

    public static String getProperty(String key) {
    	
        if (null != proMap && !org.apache.commons.lang3.StringUtils.isEmpty(proMap.get(key))) {
            return proMap.get(key);
        }
        if (null == props) {
            loadProps();
        }
        Enumeration en = props.propertyNames();
        while (en.hasMoreElements()) {
            String ekey = (String) en.nextElement();
            String value = props.getProperty(ekey);
            proMap.put(ekey, value);
        }
        return proMap.get(key);
    }
}
