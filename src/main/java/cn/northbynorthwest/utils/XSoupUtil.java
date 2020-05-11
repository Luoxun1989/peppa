package cn.northbynorthwest.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import us.codecraft.xsoup.Xsoup;

/**
 * “Go Further进无止境” <br>
 * 〈对模板中特殊XPATH进行预先处理 〉
 * 模板中xpath 可以配置为json格式，CONTAINS_STRING 和 ID_IN_URL 不能同时出现
 *
 * @author Luoxun
 * @create 2020/5/11
 * @since 1.0.0
 */
public class XSoupUtil {
    private final static String XPATH_MARK = "xpath";
    private final static String ID_IN_URL = "id-in-url";//默认优先级最后4,value可以是1、y、yes、true等表示肯定的词汇
    private final static String SUBSTRING_AFTER = "result-after";//默认优先级2
    private final static String SUBSTRING_BEFORE = "result-before"; // 默认优先级最高1
    private final static String CONTAINS_STRING = "result-contains";//默认优先级3

    //Xsoup不支持原XPATH语法，例如substring-before()等
    public static String compile(String xpath, Document document) {
        if (StringUtils.isEmpty(xpath)){
            return "";
        }
        String parserResult;
        if (xpath.contains("{") && xpath.contains("}")) {
            JSONObject jsonObject = JSON.parseObject(xpath);
            String newXpath = jsonObject.getString(XPATH_MARK);
            if (StringUtils.isEmpty(newXpath)) {
                throw new IllegalArgumentException(xpath);
            }
            String afterOpString = jsonObject.getString(SUBSTRING_AFTER);
            String beforeOpString = jsonObject.getString(SUBSTRING_BEFORE);
            String containsOpString = jsonObject.getString(CONTAINS_STRING);
            String isIdExtractOpAction = jsonObject.getString(ID_IN_URL);

            parserResult = Xsoup.compile(newXpath).evaluate(document).get();
            if (StringUtils.isEmpty(parserResult)) {
                return parserResult;
            }
            if (!StringUtils.isEmpty(afterOpString)) {
                parserResult = parserResult.split(afterOpString)[1];
            } else {
                //do nothing
            }
            if (!StringUtils.isEmpty(beforeOpString)) {
                parserResult = parserResult.split(beforeOpString)[0];
            } else {
                //do nothing
            }
            if (!StringUtils.isEmpty(containsOpString)) {
                if (parserResult.contains(containsOpString)) {
                    return "true";
                } else {
                    return "false";
                }
            } else {
                //do nothing
            }
            if (!StringUtils.isEmpty(isIdExtractOpAction)) {
                return StringOperateUtil.extractIdFromUrl(parserResult);
            } else {
                //do nothing
            }
            return parserResult;
        } else {
            return Xsoup.compile(xpath).evaluate(document).get();
        }
    }
}
