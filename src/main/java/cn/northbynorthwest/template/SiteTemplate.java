package cn.northbynorthwest.template;

import lombok.Getter;
import lombok.Setter;
import org.dom4j.Element;

/**
 * “Go Further进无止境” <br>
 * 〈〉
 *
 * @author Luoxun
 * @create 2020/5/7
 * @since 1.0.0
 */
@Getter
@Setter
public abstract class SiteTemplate {
    private String siteId;
    private String siteName;
    private String encoding;
    private String domin;

    public abstract void parserPages(Element element);
}
