package cn.northbynorthwest.template;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * “Go Further进无止境” <br>
 * 〈页面模板类〉
 *
 * @author Luoxun
 * @create 2020/4/30
 * @since 1.0.0
 */
@Setter
@Getter
public class BookPageTemplate {
    private Map<String, String> nodeValueMap = null;   //XPATH节点和节点值
    private Map<String, String> nodeXpathMap = null;   //XPATH节点和XPATH路径
    private String pageType="html";//json、html、txt格式
    private String pageAttribute;//详情页、章节页、阅读页、评论页等
    private String request="get";//页面请求方式  默认get
    private String regexUrl;//页面统一URL

    public BookPageTemplate(String regexUrl, String pageType,String request){
        this.regexUrl = regexUrl;
        this.pageType = pageType;
        this.request = request;
    }
}
