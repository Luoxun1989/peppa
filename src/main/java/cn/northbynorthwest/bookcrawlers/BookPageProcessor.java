package cn.northbynorthwest.bookcrawlers;

import cn.northbynorthwest.constants.Constant;
import cn.northbynorthwest.pipeline.FilePipeline;
import cn.northbynorthwest.template.BookPageTemplate;
import cn.northbynorthwest.template.BookSiteTemplate;
import cn.northbynorthwest.template.PageAttributeEnum;
import cn.northbynorthwest.template.XMLTemplateParser;
import cn.northbynorthwest.utils.LoadPropertiesFileUtil;
import cn.northbynorthwest.utils.StringMatcherUtil;
import lombok.Getter;
import lombok.Setter;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;

import static cn.northbynorthwest.template.PageAttributeEnum.*;

/**
 * “Go Further进无止境” <br>
 * 〈〉
 *
 * @author Luoxun
 * @create 2020/4/29
 * @since 1.0.0
 */
@Getter
@Setter
public class BookPageProcessor implements PageProcessor {

    private Site site = Site.me().setRetryTimes(LoadPropertiesFileUtil.getIntValue(Constant.DOWNLOADER_RETRYTIMES))
            .setSleepTime(LoadPropertiesFileUtil.getIntValue(Constant.DOWNLOADER_SLEEPTIME))
            .setCycleRetryTimes(LoadPropertiesFileUtil.getIntValue(Constant.DOWNLOADER_CYCLERETRYTIMES))
            .setRetrySleepTime(LoadPropertiesFileUtil.getIntValue(Constant.DOWNLOADER_RETRYSLEEPTIME))
            .setTimeOut(LoadPropertiesFileUtil.getIntValue(Constant.DOWNLOADER_TIMEOUT));

    private BookSiteTemplate bookSiteTemplate = null;
    private String contentRegexUrl;
    private String chapterRegexUrl;
    private String readingRegexUrl;
    private String commentRegexUrl;
    private String rankingRegexUrl;

    @Override
    public void process(Page page) {


//        page.addTargetRequests(page.getHtml().links().regex("(https://blog\\.csdn\\.net/[\\w\\-]+/boo/details/\\d+)").all());
//        page.addTargetRequests(page.getHtml().links().regex("(https://book\\.qidian\\.com/info/\\d+)").all());
        page.addTargetRequests(page.getHtml().links().regex(this.contentRegexUrl).all());
//        page.addTargetRequests(page.getHtml().links().regex("(https://book\\.qidian\\.com/info/\\d+/#Catalog)").all());
        page.addTargetRequests(page.getHtml().links().regex(this.chapterRegexUrl).all());
        String url = page.getUrl().get();
        if (StringMatcherUtil.match(url, this.contentRegexUrl)) {
            parserContentPageInfo(page);
        } else if (StringMatcherUtil.match(url, this.chapterRegexUrl)) {
            parserChapterPageInfo(page);
        } else if (StringMatcherUtil.match(url, this.readingRegexUrl)) {
            parserReadingPageInfo(page);//TODO
        } else if (StringMatcherUtil.match(url, this.commentRegexUrl)) {
            parserCommentPageInfo(page);//TODO
        } else if (StringMatcherUtil.match(url, this.rankingRegexUrl)) {
            parserRankingPageInfo(page);//TODO
        }else{
            parserOthersPageInfo(page);//TODO
        }

    }

    private void parserContentPageInfo(Page page) {
        BookPageTemplate bookPageTemplate = this.bookSiteTemplate.getBookPageTemplateMap().get(PageAttributeEnum.CONTENTPAGE.name());
        String bookNameXpath = bookPageTemplate.getNodeXpathMap().get("bookName");
        String authorXpath = bookPageTemplate.getNodeXpathMap().get("author");
        String pseudonymXpath = bookPageTemplate.getNodeXpathMap().get("pseudonym");
        String bookIdXpath = bookPageTemplate.getNodeXpathMap().get("bookId");
        String bookUrlXpath = bookPageTemplate.getNodeXpathMap().get("bookUrl");
        String isFinishXpath = bookPageTemplate.getNodeXpathMap().get("isFinish");
        String wordCountsXpath = bookPageTemplate.getNodeXpathMap().get("wordCounts");
        String priceXpath = bookPageTemplate.getNodeXpathMap().get("price");
        String classNameXpath = bookPageTemplate.getNodeXpathMap().get("tags");
        String tagsXpath = bookPageTemplate.getNodeXpathMap().get("className");
        String introductionXpath = bookPageTemplate.getNodeXpathMap().get("introduction");
        String chapterListUrlXpath = bookPageTemplate.getNodeXpathMap().get("chapterListUrl");

        page.putField("bookName", page.getHtml().xpath(bookNameXpath).toString());
        if (page.getResultItems().get("bookName") == null) {
            //skip this page
            page.setSkip(true);
        }
        page.putField("text_name", page.getHtml().xpath("//*[@id='mainBox']/main/div[1]/div/div/div[1]/h1").toString());
        page.putField("write_time", page.getHtml().xpath("//*[@id='mainBox']/main/div[1]/div/div/div[2]/div[2]/span[1]"));
    }
    private void parserChapterPageInfo(Page page) {
    }
    private void parserReadingPageInfo(Page page) {
    }
    private void parserCommentPageInfo(Page page) {
    }
    private void parserRankingPageInfo(Page page) {
    }
    private void parserOthersPageInfo(Page page) {
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: BookPageProcessor mark");
            return;
        }
        XMLTemplateParser xmlTemplateParser = XMLTemplateParser.getInstance();
        xmlTemplateParser.loadXML("");
        BookPageProcessor bookPageProcessor = new BookPageProcessor();
        bookPageProcessor.bookSiteTemplate = findSiteTemplateBySiteId(xmlTemplateParser.getSiteTemplates(), args[0]);
        bookPageProcessor.contentRegexUrl = bookPageProcessor.bookSiteTemplate.getBookPageTemplateMap().get(PageAttributeEnum.CONTENTPAGE.name()).getRegexUrl();
        bookPageProcessor.chapterRegexUrl = bookPageProcessor.bookSiteTemplate.getBookPageTemplateMap().get(PageAttributeEnum.CHAPTERPAGE.name()).getRegexUrl();
//        bookPageProcessor.readingRegexUrl = bookPageProcessor.bookSiteTemplate.getBookPageTemplateMap().get(PageAttributeEnum.READINGPAGE.name()).getRegexUrl();
//        bookPageProcessor.commentRegexUrl = bookPageProcessor.bookSiteTemplate.getBookPageTemplateMap().get(PageAttributeEnum.COMMENTPAGE.name()).getRegexUrl();
//        bookPageProcessor.rankingRegexUrl = bookPageProcessor.bookSiteTemplate.getBookPageTemplateMap().get(PageAttributeEnum.RANKINGPAGE.name()).getRegexUrl();
        Spider.create(new BookPageProcessor()).addUrl("https://blog.csdn.net").addPipeline(
                new FilePipeline("J:\\workspace\\peppa\\out\\production\\peppa\\generated")).thread(5).run();
    }


    private static BookSiteTemplate findSiteTemplateBySiteId(List<BookSiteTemplate> templates, String siteId) {
        if (templates == null || templates.size() < 1) {
            return null;
        }
        for (int i = 0; i < templates.size(); i++) {
            if (siteId.equals(templates.get(i).getSiteId())) {
                return templates.get(i);
            }
        }
        return null;
    }
}
