package cn.northbynorthwest.bookcrawlers;

import cn.northbynorthwest.bookcrawlers.book.Chapter;
import cn.northbynorthwest.bookcrawlers.book.ElectronicBook;
import cn.northbynorthwest.constants.Constant;
import cn.northbynorthwest.pipeline.EBookFilePipeline;
import cn.northbynorthwest.template.BookPageTemplate;
import cn.northbynorthwest.template.BookSiteTemplate;
import cn.northbynorthwest.template.PageAttributeEnum;
import cn.northbynorthwest.template.XMLTemplateParser;
import cn.northbynorthwest.utils.LoadPropertiesFileUtil;
import cn.northbynorthwest.utils.StringMatcherUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.codec.digest.DigestUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.xsoup.Xsoup;

import java.util.ArrayList;
import java.util.List;


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

    private static BookSiteTemplate bookSiteTemplate = null;
    private static String contentRegexUrl;
    private static String chapterRegexUrl;
    private static String readingRegexUrl;
    private static String commentRegexUrl;
    private static String rankingRegexUrl;

    @Override
    public void process(Page page) {

        List<String> contentUrls = page.getHtml().links().regex(chapterRegexUrl).all();
        List<String> chapterUrls = new ArrayList<>(10);
        for (int i = 0; i < contentUrls.size(); i++) {
            chapterUrls.add(contentUrls.get(i) + chapterRegexUrl);
        }
        page.addTargetRequests(page.getHtml().links().regex(contentRegexUrl).all());
//        page.addTargetRequests(page.getHtml().links().regex(chapterRegexUrl).all());
        page.addTargetRequests(chapterUrls);
        String url = page.getUrl().get();
        if (StringMatcherUtil.match(url, contentRegexUrl)) {
            parserContentPageInfo(page);
        } else if (StringMatcherUtil.match(url, chapterRegexUrl)) {
            parserChapterPageInfo(page);
        } else if (StringMatcherUtil.match(url, readingRegexUrl)) {
            parserReadingPageInfo(page);//TODO
        } else if (StringMatcherUtil.match(url, commentRegexUrl)) {
            parserCommentPageInfo(page);//TODO
        } else if (StringMatcherUtil.match(url, rankingRegexUrl)) {
            parserRankingPageInfo(page);//TODO
        } else {
            parserOthersPageInfo(page);//TODO
        }

    }

    private void parserContentPageInfo(Page page) {

        BookPageTemplate bookPageTemplate = this.bookSiteTemplate.getBookPageTemplateMap().get(PageAttributeEnum.CONTENTPAGE.name());

        String bookNameXpath = bookPageTemplate.getNodeXpathMap().get("bookName");
        String htmlString = page.getRawText();
        Document document = Jsoup.parse(htmlString);

        String bookName = Xsoup.compile(bookNameXpath).evaluate(document).get();
        if (bookName == null || bookName.isEmpty()) {
            page.setSkip(true);
            return;
        }
        String authorXpath = bookPageTemplate.getNodeXpathMap().get("author");
        String pseudonymXpath = bookPageTemplate.getNodeXpathMap().get("pseudonym");
        String bookIdXpath = bookPageTemplate.getNodeXpathMap().get("bookId");
        String bookUrlXpath = bookPageTemplate.getNodeXpathMap().get("bookUrl");
        String isFinishXpath = bookPageTemplate.getNodeXpathMap().get("isFinish");
        String wordCountsXpath = bookPageTemplate.getNodeXpathMap().get("wordCounts");
        String priceXpath = bookPageTemplate.getNodeXpathMap().get("price");
        String tagsXpath = bookPageTemplate.getNodeXpathMap().get("tags");
        String classNameXpath = bookPageTemplate.getNodeXpathMap().get("className");
        String introductionXpath = bookPageTemplate.getNodeXpathMap().get("introduction");
        String chapterListUrlXpath = bookPageTemplate.getNodeXpathMap().get("chapterListUrl");


        ElectronicBook eBook = new ElectronicBook();

        eBook.setSiteId(this.bookSiteTemplate.getSiteId());
        eBook.setSiteName(this.bookSiteTemplate.getSiteName());
        eBook.setBookName(bookName);

        eBook.setAuthor(Xsoup.compile(authorXpath).evaluate(document).get());
        eBook.setPseudonym(Xsoup.compile(pseudonymXpath).evaluate(document).get());
        eBook.setBookId(Xsoup.compile(bookIdXpath).evaluate(document).get());
        eBook.setBookUrl(Xsoup.compile(bookUrlXpath).evaluate(document).get());
        eBook.setFinish(StringMatcherUtil.string2Boolean(Xsoup.compile(isFinishXpath).evaluate(document).get()));
        eBook.setWordCounts(StringMatcherUtil.string2Integer(Xsoup.compile(wordCountsXpath).evaluate(document).get()));
        eBook.setPrice(StringMatcherUtil.string2Float(Xsoup.compile(priceXpath).evaluate(document).get()));
        eBook.setTag(Xsoup.compile(tagsXpath).evaluate(document).get());
        eBook.setClassName(Xsoup.compile(classNameXpath).evaluate(document).get());
        eBook.setIntroduction(Xsoup.compile(introductionXpath).evaluate(document).get());
        eBook.setChapterListUrl(Xsoup.compile(chapterListUrlXpath).evaluate(document).get());

        page.putField(DigestUtils.md5Hex(page.getUrl().get()), eBook);
    }

    private void parserChapterPageInfo(Page page) {
        BookPageTemplate bookPageTemplate = this.bookSiteTemplate.getBookPageTemplateMap().get(PageAttributeEnum.CHAPTERPAGE.name());

        String tableMainNodeXpath = bookPageTemplate.getNodeXpathMap().get("tableMainNode");

        String html = page.getRawText();
        Document document = Jsoup.parse(html);

        if (null == tableMainNodeXpath || tableMainNodeXpath.isEmpty()) {
            Chapter chapter = extractChapterInfo(bookPageTemplate, document);
            if (null == chapter) {
                page.setSkip(true);
                return;
            }
            page.putField(DigestUtils.md5Hex(chapter.getChapterUrl()), chapter);
        } else {
            boolean chapterPageIsValid = false;
            if (tableMainNodeXpath.contains(";")) {
                String[] tableMainNodeXpathList = tableMainNodeXpath.split(";");
                for (String tableXpath : tableMainNodeXpathList) {
                    List<String> chapterList = Xsoup.compile(tableXpath).evaluate(document).list();
                    for (int i = 0; i < chapterList.size(); i++) {
                        Document doc = Jsoup.parse(chapterList.get(i));
                        Chapter chapter = extractChapterInfo(bookPageTemplate, doc);
                        if (null == chapter) {
                            continue;
                        }
                        chapterPageIsValid = true;
                        page.putField(getKeyChapterUrl(page.getUrl().get(), chapter.getChapterUrl(), chapter.getChapterId(),
                                chapter.getChapterName()), chapter);
                    }
                }
            } else {
                List<String> chapterList = Xsoup.compile(tableMainNodeXpath).evaluate(document).list();
                for (int i = 0; i < chapterList.size(); i++) {
                    Document doc = Jsoup.parse(chapterList.get(i));
                    Chapter chapter = extractChapterInfo(bookPageTemplate, doc);
                    if (null == chapter) {
                        continue;
                    }
                    chapterPageIsValid = true;
                    page.putField(getKeyChapterUrl(page.getUrl().get(), chapter.getChapterUrl(), chapter.getChapterId(),
                            chapter.getChapterName()), chapter);
                }
            }
            if (!chapterPageIsValid) {
                page.setSkip(true);
            }
        }
    }

    private String getKeyChapterUrl(String chapterListUrl, String chapterId, String chapterUrl, String chapterName) {
        if (null != chapterUrl && chapterUrl.length() > 0) {
            return DigestUtils.md5Hex(chapterUrl);
        } else if (null != chapterId && chapterId.length() > 0) {
            return DigestUtils.md5Hex(chapterListUrl + chapterId);
        } else {
            return DigestUtils.md5Hex(chapterListUrl + chapterName);
        }
    }

    private Chapter extractChapterInfo(BookPageTemplate bookPageTemplate, Document document) {

        String chapterNameXpath = bookPageTemplate.getNodeXpathMap().get("chapterName");
        String bookIdXpath = bookPageTemplate.getNodeXpathMap().get("bookId4Chapter");
        String chapterIdXpath = bookPageTemplate.getNodeXpathMap().get("chapterId");
        String chapterUrlXpath = bookPageTemplate.getNodeXpathMap().get("chapterUrl");
        String chapterWordCountsXpath = bookPageTemplate.getNodeXpathMap().get("chapterWordCounts");
        String writeTimeXpath = bookPageTemplate.getNodeXpathMap().get("writeTime");

        String chapterName = Xsoup.compile(chapterNameXpath).evaluate(document).get();
        if (chapterName == null || chapterName.isEmpty()) {
            return null;
        }
        Chapter chapter = new Chapter();
        chapter.setSiteId(this.bookSiteTemplate.getSiteId());
        chapter.setChapterName(chapterName);
        chapter.setBookId4Chapter(Xsoup.compile(bookIdXpath).evaluate(document).get());
        chapter.setChapterId(Xsoup.compile(chapterIdXpath).evaluate(document).get());
        chapter.setChapterUrl(Xsoup.compile(chapterUrlXpath).evaluate(document).get());
        chapter.setChapterWordCounts(StringMatcherUtil.string2Integer(Xsoup.compile(chapterWordCountsXpath).evaluate(document).get()));
        chapter.setWriteTime(Xsoup.compile(writeTimeXpath).evaluate(document).get());
        return chapter;
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
        /*if (args.length == 0) {
            System.err.println("Usage: BookPageProcessor mark");
            return;
        }*/
        XMLTemplateParser xmlTemplateParser = XMLTemplateParser.getInstance();
        xmlTemplateParser.loadXML("J:\\workspace\\peppa\\conf\\book_template.xml");
        bookSiteTemplate = findSiteTemplateBySiteId(xmlTemplateParser.getSiteTemplates(), "10001");
        contentRegexUrl = bookSiteTemplate.getBookPageTemplateMap().get(PageAttributeEnum.CONTENTPAGE.name()).getRegexUrl();
        chapterRegexUrl = bookSiteTemplate.getBookPageTemplateMap().get(PageAttributeEnum.CHAPTERPAGE.name()).getRegexUrl();
        System.out.println(contentRegexUrl);
//        readingRegexUrl = bookPageProcessor.bookSiteTemplate.getBookPageTemplateMap().get(PageAttributeEnum.READINGPAGE.name()).getRegexUrl();
//        commentRegexUrl = bookPageProcessor.bookSiteTemplate.getBookPageTemplateMap().get(PageAttributeEnum.COMMENTPAGE.name()).getRegexUrl();
//        rankingRegexUrl = bookPageProcessor.bookSiteTemplate.getBookPageTemplateMap().get(PageAttributeEnum.RANKINGPAGE.name()).getRegexUrl();
        Spider.create(new BookPageProcessor()).addUrl("https://www.qidian.com").addPipeline(
                new EBookFilePipeline("J:\\workspace\\peppa\\out\\production\\peppa\\generated")).thread(2).run();
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
