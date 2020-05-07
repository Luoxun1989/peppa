package cn.northbynorthwest.template;

import cn.northbynorthwest.constants.Constant;
import cn.northbynorthwest.utils.LoadPropertiesFileUtil;
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
public class BookSiteTemplate extends SiteTemplate{
    private List<BookPageTemplate> bookPageTemplates = null;
    private static final String  GET_REQUEST="get";
    private static final String  HTML_FORMAT="html";

    public BookSiteTemplate() {

    }
    @Override
    public void parserPages(Element element) {
        if (element == null) {
            return;
        }
        List<Element> pageElements = element.elements(LoadPropertiesFileUtil.getStringValue(Constant.SITE_PAGE_DIV));
        for (Element elem : pageElements) {
            String pageClass = elem.attributeValue(LoadPropertiesFileUtil.getStringValue(Constant.SITE_PAGE_ATTRIBUTE_ATTR));
            if (PageAttributeEnum.CONTENTPAGE.name().equals(pageClass)) {
                parserContentPages(elem);
            } else if (PageAttributeEnum.CHAPTERPAGE.name().equals(pageClass)) {
                parserChapterPages(elem);
            } else if (PageAttributeEnum.READINGPAGE.name().equals(pageClass)) {
                parserReadingPages(elem);
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
        String regexUrl = elem.attributeValue(LoadPropertiesFileUtil.getStringValue(Constant.SITE_PAGE_REGEXURL_ATTR));
        if (null == regexUrl || regexUrl.isEmpty()) {
            //log
            return;
        }
        String request = elem.attributeValue(LoadPropertiesFileUtil.getStringValue(Constant.SITE_PAGE_REQUEST_ATTR));
        if (null == request || request.isEmpty()) {
            request = GET_REQUEST;
        }
        String responseBody = elem.attributeValue(LoadPropertiesFileUtil.getStringValue(Constant.SITE_PAGE_RESPONSEBODY_ATTR));
        if (null == responseBody || responseBody.isEmpty()) {
            responseBody = HTML_FORMAT;
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
        if (bookPageTemplates == null) {
            bookPageTemplates = new ArrayList<>();
        }
        bookPageTemplates.add(new BookPageTemplate(responseBody, request));

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
            request = GET_REQUEST;
        }
        String responseBody = elem.attributeValue("responseBody");
        if (null == responseBody || responseBody.isEmpty()) {
            responseBody = HTML_FORMAT;
        }
        
        String chapterNameXpath = elem.elementTextTrim("chapterName");
        String chapterIdXpath = elem.elementTextTrim("chapterId");
        String chapterWordsXpath = elem.elementTextTrim("chapterWords");
        String writeTimeXpath = elem.elementTextTrim("writeTime");
        String chapterUrlXpath = elem.elementTextTrim("chapterUrl");


        if (bookPageTemplates == null) {
            bookPageTemplates = new ArrayList<>();
        }
        bookPageTemplates.add(new BookPageTemplate(responseBody, request));
    }
}
