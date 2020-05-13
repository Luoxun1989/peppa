package cn.northbynorthwest.bookcrawlers;


import cn.northbynorthwest.db.JedisPoolManager;
import cn.northbynorthwest.template.PageAttributeEnum;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.xsoup.Xsoup;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * “Go Further进无止境” <br>
 * 〈〉
 *
 * @author Luoxun
 * @create 2020/4/30
 * @since 1.0.0
 */

public class SinaBlogProcessor {

    public static final String URL_LIST = "http://blog\\.sina\\.com\\.cn/s/articlelist_1487828712_0_\\d+\\.html";

    public static final String URL_POST = "http://blog\\.sina\\.com\\.cn/s/blog_\\w+\\.html";

    public static void main(String[] args) {
       /* String html = "J:\\workspace\\peppa\\conf\\22.html";
        File f = new File(html);
        if (!f.exists()){
            return;
        }
        String strs="";
        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(html));
            byte[] bytes = new byte[2048];
            //接受读取的内容(n就代表的相关数据，只不过是数字的形式)
            int n = -1;
            //循环取出数据
            while ((n = in.read(bytes,0,bytes.length)) != -1) {
                //转换成字符串
                String str = new String(bytes,0,n,"UTF-8");
                strs += str;
//                System.out.println(str);
            }
            //关闭流
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Document document = Jsoup.parse(strs);

        String result = Xsoup.compile("//a/@href").evaluate(document).get();
        System.out.println(result);

        List<String> list = Xsoup.compile("//*[@id='j-catalogWrap']/div[1]/div/ul/li").evaluate(document).list();
        for (int i = 0; i < list.size(); i++) {
            Document doc = Jsoup.parse(list.get(i));
            System.out.println(Xsoup.compile("//a/@href").evaluate(doc).get());
            System.out.println(Xsoup.compile("//a/text()").evaluate(doc).get());
            System.out.println(Xsoup.compile("//a/@title").evaluate(doc).get());
        }
//        List<String> list2 = Xsoup.compile("//*[@id='j-catalogWrap']/div[2]/div/ul/li/a/text()").evaluate(document).list();
//        for (int i = 0; i < list2.size(); i++) {
//            System.out.println(list2.get(i));
//        }*/
        JedisPool jedisPool = JedisPoolManager.getRedisPool();

        Jedis jedis = jedisPool.getResource();
        Map<String,String> cartInfo = new HashMap<>();
        cartInfo.put("10088","1");
        cartInfo.put("10099","2");
//        jedis.hmset("cart:1001",cartInfo);
//        jedis.hset("cart:1001","10089","3");
        System.out.println(jedis.hget("cart:1001","10088"));
        System.out.println(jedis.hget("cart:1001","10099"));
        System.out.println(jedis.hgetAll("cart:1001"));
        System.out.println(jedis.hmget("cart:1001","10088","10099"));
        System.out.println(jedis.hlen("cart:1001"));
        String key = "queue_www.qidian.com";
        System.out.println(jedis.lpop(key));
//        System.out.println(jedis.hlen("queue_www.qidian.com".getBytes()));
//        System.out.println(jedis.llen("set_www.qidian.com"));
        jedis.close();
    }
}
