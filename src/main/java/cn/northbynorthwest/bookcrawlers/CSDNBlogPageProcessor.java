package cn.northbynorthwest.bookcrawlers;

import cn.northbynorthwest.pipeline.ConsolePipeline;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * “Go Further进无止境” <br>
 * 〈〉
 *
 * @author Luoxun
 * @create 2020/4/29
 * @since 1.0.0
 */
public class CSDNBlogPageProcessor implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(3000);

    @Override
    public void process(Page page) {
        page.addTargetRequests(page.getHtml().links().regex("(https://blog\\.csdn\\.net/^[0-9a-zA-Z_]{1,}$/article/details/\\d+)").all());
        page.putField("author_name", page.getHtml().xpath("//*[@id='mainBox']/main/div[1]/div/div/div[2]/div[1]/div/a[1]").toString());
        page.putField("text_name", page.getHtml().xpath("//*[@id='mainBox']/main/div[1]/div/div/div[1]/h1").toString());
        if (page.getResultItems().get("text_name")==null){
            //skip this page
            System.out.println("skip this page");
            page.setSkip(true);
        }

        page.putField("write_time", page.getHtml().xpath("//*[@id='mainBox']/main/div[1]/div/div/div[2]/div[2]/span[1]"));
    }

    @Override
    public Site getSite() {
        return site;
    }
    public static void main(String[] args) {
        Spider.create(new CSDNBlogPageProcessor()).addUrl("https://blog.csdn.net").addPipeline(new ConsolePipeline()).thread(5).run();
    }
}
