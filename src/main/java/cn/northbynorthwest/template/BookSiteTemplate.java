package cn.northbynorthwest.template;

import cn.northbynorthwest.constants.Constant;
import cn.northbynorthwest.utils.LoadPropertiesFileUtil;
import lombok.Getter;
import lombok.Setter;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class BookSiteTemplate extends SiteTemplate {
    private Map<String, BookPageTemplate> bookPageTemplateMap = null;
    private static final String GET_REQUEST = "get";
    private static final String HTML_FORMAT = "html";

    public BookSiteTemplate() {

    }

    @Override
    public void parserPagesXpath(Element element) {
        if (element == null) {
            return;
        }
        List<Element> pageElements = element.elements(LoadPropertiesFileUtil.getStringValue(Constant.SITE_PAGE_DIV));
        for (Element elem : pageElements) {
            String pageAttribute = elem.attributeValue(LoadPropertiesFileUtil.getStringValue(Constant.SITE_PAGE_ATTRIBUTE_ATTR));
            if (null == pageAttribute || pageAttribute.isEmpty()) {
                //log
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
            String pageType = elem.attributeValue(LoadPropertiesFileUtil.getStringValue(Constant.SITE_PAGE_PAGETYPE_ATTR));
            if (null == pageType || pageType.isEmpty()) {
                pageType = HTML_FORMAT;
            }
            if (PageAttributeEnum.CONTENTPAGE.name().equals(pageAttribute)) {
                parserContentPagesXpath(elem, regexUrl, pageType, request);
            } else if (PageAttributeEnum.CHAPTERPAGE.name().equals(pageAttribute)) {
                parserChapterPagesXpath(elem, regexUrl, pageType, request);
            } else if (PageAttributeEnum.READINGPAGE.name().equals(pageAttribute)) {
                parserReadingPagesXpath(elem);
            } else {
                parserDefaultPagesXpath(elem);
            }
        }

    }

    private void parserDefaultPagesXpath(Element elem) {
        if (elem == null) {
            return;
        }
    }

    private void parserContentPagesXpath(Element elem, String regexUrl, String pageType, String request) {
        if (elem == null) {
            return;
        }

        String bookNameXpath = elem.elementTextTrim("bookName");
        String authorXpath = elem.elementTextTrim("author");
        String pseudonymXpath = elem.elementTextTrim("pseudonym");
        String bookIdXpath = elem.elementTextTrim("bookId");
        String bookUrlXpath = elem.elementTextTrim("bookUrl");
        String finishXpath = elem.elementTextTrim("isFinish");
        String wordCountsXpath = elem.elementTextTrim("wordCounts");
        String priceXpath = elem.elementTextTrim("price");
        String tagsXpath = elem.elementTextTrim("tags");
        String classNameXpath = elem.elementTextTrim("className");
        String introductionXpath = elem.elementTextTrim("introduction");
        String chapterListUrlXpath = elem.elementTextTrim("chapterListUrl");
        BookPageTemplate bookPageTemplate = new BookPageTemplate(regexUrl, pageType, request);
        Map<String, String> nodeXpathMap = new HashMap<String, String>(10);
        nodeXpathMap.put("bookName", bookNameXpath);
        nodeXpathMap.put("author", authorXpath);
        nodeXpathMap.put("pseudonym", pseudonymXpath);
        nodeXpathMap.put("bookId", bookIdXpath);
        nodeXpathMap.put("bookUrl", bookUrlXpath);
        nodeXpathMap.put("isFinish", finishXpath);
        nodeXpathMap.put("wordCounts", wordCountsXpath);
        nodeXpathMap.put("price", priceXpath);
        nodeXpathMap.put("tags", tagsXpath);
        nodeXpathMap.put("className", classNameXpath);
        nodeXpathMap.put("introduction", introductionXpath);
        nodeXpathMap.put("chapterListUrl", chapterListUrlXpath);
        bookPageTemplate.setNodeXpathMap(nodeXpathMap);
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
        if (bookPageTemplateMap == null) {
            bookPageTemplateMap = new HashMap<>();
        }
        bookPageTemplateMap.put(PageAttributeEnum.CONTENTPAGE.name(),bookPageTemplate);

    }

    //TODO
    private void parserPlayingPagesXpath(Element elem) {
        if (elem == null) {
            return;
        }
    }

    //TODO
    private void parserReadingPagesXpath(Element elem) {
        if (elem == null) {
            return;
        }
    }

    private void parserChapterPagesXpath(Element elem, String regexUrl, String pageType, String request) {

        if (elem == null) {
            return;
        }

        String chapterNameXpath = elem.elementTextTrim("chapterName");
        String chapterIdXpath = elem.elementTextTrim("chapterId");
        String chapterWordsXpath = elem.elementTextTrim("chapterWords");
        String writeTimeXpath = elem.elementTextTrim("writeTime");
        String chapterUrlXpath = elem.elementTextTrim("chapterUrl");
        BookPageTemplate bookPageTemplate = new BookPageTemplate(regexUrl, pageType, request);
        Map<String, String> nodeXpathMap = new HashMap<String, String>(5);
        nodeXpathMap.put("chapterName", chapterNameXpath);
        nodeXpathMap.put("chapterId", chapterIdXpath);
        nodeXpathMap.put("chapterWords", chapterWordsXpath);
        nodeXpathMap.put("writeTime", writeTimeXpath);
        nodeXpathMap.put("chapterUrl", chapterUrlXpath);
        bookPageTemplate.setNodeXpathMap(nodeXpathMap);

        if (bookPageTemplateMap == null) {
            bookPageTemplateMap = new HashMap<>();
        }
        bookPageTemplateMap.put(PageAttributeEnum.CHAPTERPAGE.name(),bookPageTemplate);
    }
}
