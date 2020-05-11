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
import cn.northbynorthwest.utils.StringOperateUtil;
import cn.northbynorthwest.utils.XSoupUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private static BookSiteTemplate bookSiteTemplate = null;
    private static String contentRegexUrl;
    private static String chapterRegexUrl;
    private static String readingRegexUrl;
    private static String commentRegexUrl;
    private static String rankingRegexUrl;
    private static String siteEntryUrlPrefix = "http:";
    private final static String XPATH_SEPARATOR = ";";
    private final static String DOMIN_URL_PREFIX = "https:";

    @Override
    public void process(Page page) {
        //正常的详情页URL，同时也必须考虑省略http或者https前缀的URL匹配
        List<String> contentUrls = StringOperateUtil.extractUseMultipleTypeRegexUrl(page,contentRegexUrl,siteEntryUrlPrefix);
//        page.addTargetRequests(contentUrls);
        //章节页初始化URL必须以http或者https开头，如果xml文件中regexUrl字段Value不是两者开头，则默认章节页初始化URL是由详情页初始化URL和
        //Value拼接获得。
        if (!chapterRegexUrl.startsWith(siteEntryUrlPrefix)) {
            List<String> chapterUrls = new ArrayList<>(10);
            if (contentUrls != null && contentUrls.size()>0){
                for (int i = 0; i < contentUrls.size(); i++) {
                    chapterUrls.add(contentUrls.get(i).concat(chapterRegexUrl));
                }
            }
            page.addTargetRequests(chapterUrls);
        } else {
            //正常的章节URL，同时也必须考虑省略http或者https前缀的URL匹配
            page.addTargetRequests(StringOperateUtil.extractUseMultipleTypeRegexUrl(page,chapterRegexUrl,siteEntryUrlPrefix));
        }

        String url = page.getUrl().get();
        logger.info("url of this page: {}, extract new urls size:{}", url, contentUrls.size());
        //chapter初始化URL如果包含content初始化URL，即chapterRegexUrl包含contentRegexUrl，则必须写在第一个if语句中。
        if (StringOperateUtil.match(url, !chapterRegexUrl.startsWith(siteEntryUrlPrefix) ? contentRegexUrl.concat(chapterRegexUrl) : chapterRegexUrl)) {
            logger.info("this is chapter page, execute chapter page parsing");
            parserChapterPageInfo(page);
        } else if (StringOperateUtil.match(url, contentRegexUrl)) {
            logger.info("this is content page, execute content page parsing");
            parserContentPageInfo(page);
        }  else if (StringOperateUtil.match(url, readingRegexUrl)) {
            parserReadingPageInfo(page);//TODO
        } else if (StringOperateUtil.match(url, commentRegexUrl)) {
            parserCommentPageInfo(page);//TODO
        } else if (StringOperateUtil.match(url, rankingRegexUrl)) {
            parserRankingPageInfo(page);//TODO
        } else {
            parserOthersPageInfo(page);//TODO
        }

    }

    private void parserContentPageInfo(Page page) {

        BookPageTemplate bookPageTemplate = bookSiteTemplate.getBookPageTemplateMap().get(PageAttributeEnum.CONTENTPAGE.name());

        String bookNameXpath = bookPageTemplate.getNodeXpathMap().get("bookName");
        String htmlString = page.getRawText();
        Document document = Jsoup.parse(htmlString);
        if (null == document) {
            logger.warn("jsoup parse html body fail");
            return;
        }
        String bookName = XSoupUtil.compile(bookNameXpath, document);
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

        eBook.setSiteId(bookSiteTemplate.getSiteId());
        eBook.setSiteName(bookSiteTemplate.getSiteName());
        eBook.setBookName(bookName);
        eBook.setAuthor(XSoupUtil.compile(authorXpath, document));
        eBook.setPseudonym(XSoupUtil.compile(pseudonymXpath, document));
        eBook.setBookUrl(StringOperateUtil.urlCompletenessCheck(page.getUrl().get(),
                XSoupUtil.compile(bookUrlXpath, document)));
        eBook.setBookId(XSoupUtil.compile(bookIdXpath, document));

        logger.info("main information of this book: {}", eBook.toMainInfoString());
        eBook.setFinish(StringOperateUtil.string2Boolean(XSoupUtil.compile(isFinishXpath, document)));
        eBook.setWordCounts(StringOperateUtil.string2Integer(XSoupUtil.compile(wordCountsXpath, document)));
        eBook.setPrice(StringOperateUtil.string2Float(XSoupUtil.compile(priceXpath, document)));
        eBook.setTag(XSoupUtil.compile(tagsXpath, document));
        eBook.setClassName(XSoupUtil.compile(classNameXpath, document));
        eBook.setIntroduction(XSoupUtil.compile(introductionXpath, document));
        eBook.setChapterListUrl(XSoupUtil.compile(chapterListUrlXpath, document));
        page.putField(DigestUtils.md5Hex(page.getUrl().get()), eBook);
    }

    private void parserChapterPageInfo(Page page) {
        BookPageTemplate bookPageTemplate = bookSiteTemplate.getBookPageTemplateMap().get(PageAttributeEnum.CHAPTERPAGE.name());

        String tableMainNodeXpath = bookPageTemplate.getNodeXpathMap().get("tableMainNode");

        String html = page.getRawText();
        Document document = Jsoup.parse(html);
        String bookIdXpath = bookPageTemplate.getNodeXpathMap().get("bookId4Chapter");
        String bookId4Chapter = XSoupUtil.compile(bookIdXpath, document);

        if (StringUtils.isEmpty(tableMainNodeXpath)) {
            logger.info("only one chapter in this page, execute parsing");
            Chapter chapter = extractChapterInfo(bookPageTemplate, document, page);
            if (null == chapter) {
                page.setSkip(true);
                return;
            }
            chapter.setBookId4Chapter(bookId4Chapter);
            page.putField(DigestUtils.md5Hex(chapter.getChapterUrl()), chapter);
        } else {
            logger.info("all chapters in this page, execute parsing");
            boolean chapterPageIsValid = false;

            if (tableMainNodeXpath.contains(XPATH_SEPARATOR)) {
                String[] tableMainNodeXpathList = tableMainNodeXpath.split(XPATH_SEPARATOR);
                for (String tableXpath : tableMainNodeXpathList) {
                    List<String> chapterList = Xsoup.compile(tableXpath).evaluate(document).list();
                    for (int i = 0; i < chapterList.size(); i++) {
                        Document doc = Jsoup.parse(chapterList.get(i));
                        Chapter chapter = extractChapterInfo(bookPageTemplate, doc, page);
                        if (null == chapter) {
                            continue;
                        }
                        chapter.setBookId4Chapter(bookId4Chapter);
                        chapterPageIsValid = true;
                        page.putField(getKeyChapterUrl(page.getUrl().get(), chapter.getChapterUrl(), chapter.getChapterId(),
                                chapter.getChapterName()), chapter);
                    }
                }
            } else {
                List<String> chapterList = Xsoup.compile(tableMainNodeXpath).evaluate(document).list();
                for (int i = 0; i < chapterList.size(); i++) {
                    Document doc = Jsoup.parse(chapterList.get(i));
                    Chapter chapter = extractChapterInfo(bookPageTemplate, doc, page);
                    if (null == chapter) {
                        continue;
                    }
                    chapter.setBookId4Chapter(bookId4Chapter);
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

    private Chapter extractChapterInfo(BookPageTemplate bookPageTemplate, Document document, Page page) {

        String chapterNameXpath = bookPageTemplate.getNodeXpathMap().get("chapterName");
//        String bookIdXpath = bookPageTemplate.getNodeXpathMap().get("bookId4Chapter");
        String chapterIdXpath = bookPageTemplate.getNodeXpathMap().get("chapterId");
        String chapterUrlXpath = bookPageTemplate.getNodeXpathMap().get("chapterUrl");
        String chapterWordCountsXpath = bookPageTemplate.getNodeXpathMap().get("chapterWordCounts");
        String writeTimeXpath = bookPageTemplate.getNodeXpathMap().get("writeTime");

        String chapterName = XSoupUtil.compile(chapterNameXpath, document);
        if (chapterName == null || chapterName.isEmpty()) {
            return null;
        }
        Chapter chapter = new Chapter();
        chapter.setSiteId(bookSiteTemplate.getSiteId());
        chapter.setChapterName(chapterName);
//        chapter.setBookId4Chapter(XSoupUtil.compile(bookIdXpath, document));
        chapter.setChapterId(XSoupUtil.compile(chapterIdXpath, document));
//        chapter.setChapterUrl(XSoupUtil.compile(chapterUrlXpath,document));
        chapter.setChapterUrl(StringOperateUtil.urlCompletenessCheck(page.getUrl().get(),
                XSoupUtil.compile(chapterUrlXpath, document)));
        chapter.setChapterWordCounts(StringOperateUtil.string2Integer(XSoupUtil.compile(chapterWordCountsXpath, document)));
        chapter.setWriteTime(XSoupUtil.compile(writeTimeXpath, document));
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
//        System.out.println(contentRegexUrl+bookSiteTemplate.getSiteId()+bookSiteTemplate.getSiteName());
//        readingRegexUrl = bookPageProcessor.bookSiteTemplate.getBookPageTemplateMap().get(PageAttributeEnum.READINGPAGE.name()).getRegexUrl();
//        commentRegexUrl = bookPageProcessor.bookSiteTemplate.getBookPageTemplateMap().get(PageAttributeEnum.COMMENTPAGE.name()).getRegexUrl();
//        rankingRegexUrl = bookPageProcessor.bookSiteTemplate.getBookPageTemplateMap().get(PageAttributeEnum.RANKINGPAGE.name()).getRegexUrl();
        if (bookSiteTemplate.getDomin().startsWith(DOMIN_URL_PREFIX)){
            siteEntryUrlPrefix = DOMIN_URL_PREFIX;
        }
        Spider.create(new BookPageProcessor()).addUrl(bookSiteTemplate.getDomin()).addPipeline(
                new EBookFilePipeline("J:\\workspace\\peppa\\out\\production\\peppa\\generated")).thread(5).run();
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
