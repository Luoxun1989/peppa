package cn.northbynorthwest.template;

import lombok.Getter;
import lombok.Setter;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * “Go Further进无止境” <br>
 * 〈站点模板类〉
 *
 * @author Luoxun
 * @create 2020/4/30
 * @since 1.0.0
 */
@Getter
@Setter
public class WebSiteTemplate {
    private String siteId;
    private String siteName;
    private String encoding;
    private List<WebPageTemplate> webPageTemplates = null;

    public WebSiteTemplate() {

    }
    enum PageClass {CONTENTPAGE, CHAPTERPAGE, READINGPAGE, PLAYINGPAGE}

    public void parserPages(Element element) {
        if (element == null) {
            return;
        }
        List<Element> pageElements = element.elements("page");
        for (Element elem : pageElements) {
            String pageClass = elem.attributeValue("pageClass");
            if (PageClass.CONTENTPAGE.name().equals(pageClass)) {
                parserContentPages(elem);
            } else if (PageClass.CHAPTERPAGE.name().equals(pageClass)) {
                parserChapterPages(elem);
            } else if (PageClass.READINGPAGE.name().equals(pageClass)) {
                parserReadingPages(elem);
            } else if (PageClass.PLAYINGPAGE.name().equals(pageClass)){
                parserPlayingPages(elem);
            } else {
                parserDefaultPages(elem);
            }
        }

    }

    private void parserDefaultPages(Element elem) {
        if (elem == null) {
            return;
        }
    }

    private void parserContentPages(Element elem) {
        if (elem == null) {
            return;
        }
        String regexUrl = elem.attributeValue("regexUrl");
        if (null == regexUrl || regexUrl.isEmpty()) {
            //log
            return;
        }
        String request = elem.attributeValue("request");
        if (null == request || request.isEmpty()) {
            request = "get";
        }
        String responseBody = elem.attributeValue("responseBody");
        if (null == responseBody || responseBody.isEmpty()) {
            responseBody = "html";
        }
        String bookNameXpath = elem.elementTextTrim("bookName");
        String authorXpath = elem.elementTextTrim("pseudonym");
        String bookidXpath = elem.elementTextTrim("bookId");
        String tagsXpath = elem.elementTextTrim("tags");
        String classNameXpath = elem.elementTextTrim("className");
        String introductionXpath = elem.elementTextTrim("introduction");
        String finishXpath = elem.elementTextTrim("isFinish");
        String chapterListUrlXpath = elem.elementTextTrim("chapterListUrl");

          /*

            String clickCountXpath = elem.elementTextTrim("clickCount");
            String recommendCountXpath = elem.elementTextTrim("recommendCount");
            String collectCountXpath = elem.elementTextTrim("collectCount");
            String commentCountXpath = elem.elementTextTrim("commentCount");
            String flowerCountXpath = elem.elementTextTrim("flowerCount");
            String wordsCountXpath = elem.elementTextTrim("wordsCount");
            String scoreXpath = elem.elementTextTrim("score");
            String isbnXpath = elem.elementTextTrim("ISBN");
            String publisherXpath = elem.elementTextTrim("publisher");
            String providerXpath = elem.elementTextTrim("provider");
            String readingCountXpath = elem.elementTextTrim("readingCount");
            String scoreCountXpath = elem.elementTextTrim("scoreCount");
            String contentXpath = elem.elementTextTrim("content");

            */
        if (webPageTemplates == null) {
            webPageTemplates = new ArrayList<>();
        }
        webPageTemplates.add(new WebPageTemplate(responseBody, request));

    }

    //TODO
    private void parserPlayingPages(Element elem) {
        if (elem == null) {
            return;
        }
    }
    //TODO
    private void parserReadingPages(Element elem) {
        if (elem == null) {
            return;
        }
    }

    private void parserChapterPages(Element elem) {
        
        if (elem == null) {
            return;
        }
        String regexUrl = elem.attributeValue("regexUrl");
        if (null == regexUrl || regexUrl.isEmpty()) {
            //log
            return;
        }
        String request = elem.attributeValue("request");
        if (null == request || request.isEmpty()) {
            request = "get";
        }
        String responseBody = elem.attributeValue("responseBody");
        if (null == responseBody || responseBody.isEmpty()) {
            responseBody = "html";
        }
        
        String chapterNameXpath = elem.elementTextTrim("chapterName");
        String chapterIdXpath = elem.elementTextTrim("chapterId");
        String chapterWordsXpath = elem.elementTextTrim("chapterWords");
        String writeTimeXpath = elem.elementTextTrim("writeTime");
        String chapterUrlXpath = elem.elementTextTrim("chapterUrl");


        if (webPageTemplates == null) {
            webPageTemplates = new ArrayList<>();
        }
        webPageTemplates.add(new WebPageTemplate(responseBody, request));
    }
}
