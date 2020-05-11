package cn.northbynorthwest.constants;

public interface Constant {
    /*
     * @Description:
     * @Date: 2020/5/7 17:50
     * @param: 
     * @return: 
     **/

    //jdbc
    String JDBC_DRIVER = "jdbc.driverClass";
    String JDBC_URL = "jdbc.connectionURL";
    String JDBC_USER = "jdbc.username";
    String JDBC_PASSWORD = "jdbc.password";
    String JDBC_POOL_INIT = "jdbc.pool.init";
    String JDBC_POOL_MINIDLE = "jdbc.pool.minIdle";
    String JDBC_POOL_MAXACTIVE = "jdbc.pool.maxActive";
    String JDBC_TESTSQL = "jdbc.testSql";

    //downloader
    String DOWNLOADER_SLEEPTIME="downloader.sleepTime";
    String DOWNLOADER_RETRYTIMES="downloader.retryTimes";
    String DOWNLOADER_CYCLERETRYTIMES="downloader.cycleRetryTimes";
    String DOWNLOADER_RETRYSLEEPTIME="downloader.retrySleepTime";
    String DOWNLOADER_TIMEOUT="downloader.timeOut";

    //site
    String SITE_TEMPLATE_ROOT="site.template.root";
    String SITE_TEMPLATE_DIV="site.template.div";
    String SITE_SITEID_ATTR="site.siteId.attr";
    String SITE_SITENAME_ATTR="site.siteName.attr";
    String SITE_ENCODING_ATTR="site.encoding.attr";
    String SITE_DOMIN_ATTR="site.domin.attr";
    String SITE_PAGE_ROOT="site.page.root";
    String SITE_PAGE_DIV="site.page.div";
    String SITE_PAGE_ATTRIBUTE_ATTR="site.page.attribute.attr";
    String SITE_PAGE_REGEXURL_ATTR="site.page.regexUrl.attr";
    String SITE_PAGE_REQUEST_ATTR="site.page.request.attr";
    String SITE_PAGE_PAGETYPE_ATTR="site.page.pageType.attr";

    String SITE_URL_SUFFIX="{}";
//    String SITE_ITEMID_PREFFIX="{}";
    //book


}
