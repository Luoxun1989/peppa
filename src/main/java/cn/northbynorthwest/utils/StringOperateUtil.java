package cn.northbynorthwest.utils;

import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * “Go Further进无止境” <br>
 * 〈字符串匹配工具类〉
 *
 * @author Luoxun
 * @create 2020/5/8
 * @since 1.0.0
 */
public class StringOperateUtil {
    private final static String HTTP_PREFIX = "http:";
    private final static String HTTPS_PREFIX = "https:";
    private final static String HTML_SUFFIX = ".html";
    private final static String XML_SUFFIX = ".xml";
    private final static String HTM_SUFFIX = ".htm";
    private final static String JSP_SUFFIX = ".html";
    private final static String[] BOOLEAN_MARK = {"1","true","yes","y"};

    /*
     * @Description:爬虫用来下载的URL链接与正则式URl链接比对，由此确定是哪一种（详情，章节，阅读？）类型的页面，
     * @Date: 2020/5/11 19:21
     * @param: [url, regexUrl]
     * @return: boolean
     **/
    public static boolean match(String url, String regexUrl) {
        if (StringUtils.isEmpty(url) || StringUtils.isEmpty(regexUrl)) {
            return false;
        }
        Pattern p = Pattern.compile(regexUrl);
        Matcher m = p.matcher(url);
        while (m.find()) {
            return true;
        }
        return false;
    }
    /*
     * @Description:URL检测，解析到的URL不一定完整（通常缺失http或者https前缀）
     * @Date: 2020/5/11 19:20
     * @param: [pageUrl, crawlerUrl]
     * @return: java.lang.String
     **/
    public static String urlCompletenessCheck(String pageUrl,String crawlerUrl) {
        if (StringUtils.isEmpty(crawlerUrl)){
            return "";
        }
        if (crawlerUrl.startsWith(HTTP_PREFIX)){
            return crawlerUrl;
        }
        if (pageUrl.startsWith(HTTPS_PREFIX)){
            return HTTPS_PREFIX.concat(crawlerUrl);
        }else {
            return HTTP_PREFIX.concat(crawlerUrl);
        }
    }

    /*
     * @Description:从URL中抽取ID
     * @Date: 2020/5/11 19:21
     * @param: [url]
     * @return: java.lang.String
     **/
    public static String extractIdFromUrl(String url) {
        if (StringUtils.isEmpty(url)){
            return "";
        }
        int indexOfBackslash = url.lastIndexOf("/");
        int indexOfPoint = url.length();
        if (url.endsWith(HTML_SUFFIX) || url.endsWith(XML_SUFFIX) || url.endsWith(HTM_SUFFIX) || url.endsWith(JSP_SUFFIX)) {
            indexOfPoint = url.lastIndexOf(".");
        }
        return url.substring(indexOfBackslash + 1, indexOfPoint);
    }

    /*
     * @Description:字符串转布尔型
     * @Date: 2020/5/11 19:21
     * @param: [var]
     * @return: boolean
     **/
    public static boolean string2Boolean(String var) {
        if (BOOLEAN_MARK[0].equals(StringUtils.strip(var).trim())) {
            return true;
        }
        if (BOOLEAN_MARK[1].equalsIgnoreCase(StringUtils.strip(var).trim())) {
            return true;
        }
        if (BOOLEAN_MARK[2].equalsIgnoreCase(StringUtils.strip(var).trim())) {
            return true;
        }
        if (BOOLEAN_MARK[3].equalsIgnoreCase(StringUtils.strip(var).trim())) {
            return true;
        }
        return false;
    }

    public static float string2Float(String var) {
        if (StringUtils.isEmpty(var)){
            return 0.0F;
        }
        return Float.valueOf(StringUtils.strip(var).trim());
    }

    //TODO
    public static int string2Integer(String var) {
        if (StringUtils.isEmpty(var)){
            return 0;
        }
//        return Integer.valueOf(string2Float(var)*10000);
        return (int) (string2Float(var)*10000);
    }

    /*
     * @Description:页面URL可能有多种类型，比如省略掉http或者https，要保证这些页面URL也能被抓取到
     * @Date: 2020/5/11 19:28
     * @param: [page, regexUrl, siteEntryUrlPrefix]
     * @return: java.util.List<java.lang.String>
     **/
    public static List<String> extractUseMultipleTypeRegexUrl(Page page,String regexUrl,String siteEntryUrlPrefix){
        List<String> result = new ArrayList<>(10);
        //待下载页面初始化URL必须以http或者https开头，正常URL举例：https://book.qidian.com/info/1012902516，其他类型页面同理
        List<String> urlsWithHttp = page.getHtml().links().regex(regexUrl).all();
        if (urlsWithHttp == null || urlsWithHttp.size() < 1) {
        }else{
            result.addAll(urlsWithHttp);
        }
        //大多数网站会省略掉http或者https，因此也要考虑例如：//book.qidian.com/info/1012902516 格式的URL，其他类型页面同理
        List<String> urlsWithoutHttp = page.getHtml().links().regex(regexUrl.replace(siteEntryUrlPrefix,"")).all();
        if (urlsWithoutHttp == null || urlsWithoutHttp.size() < 1) {
        }else{
            for (int i = 0; i < urlsWithoutHttp.size(); i++) {
                result.add(siteEntryUrlPrefix.concat(urlsWithoutHttp.get(i)));
            }
        }
        return result;
    }

}
