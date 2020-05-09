package cn.northbynorthwest.template;

import cn.northbynorthwest.constants.Constant;
import cn.northbynorthwest.utils.LoadPropertiesFileUtil;
import cn.northbynorthwest.utils.PathParserUtil;
import lombok.Getter;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * “Go Further进无止境” <br>
 * 〈XML文件模板解析〉
 *
 * @author Luoxun
 * @create 2020/5/7
 * @since 1.0.0
 */

@Getter
public class XMLTemplateParser {
    private static XMLTemplateParser xmlFileParser = null;
    private List<BookSiteTemplate> siteTemplates = null;
    public static XMLTemplateParser getInstance()
    {
        synchronized (XMLTemplateParser.class){
            if (xmlFileParser == null)
            {
                xmlFileParser = new XMLTemplateParser();
            }
        }
        return xmlFileParser;
    }
    private XMLTemplateParser(){
    }

    public void loadXML(String filePath){
        File f = new File(filePath);
        if(!f.exists()){
            //log
            return;
        }
        if (f.isDirectory()){
            loadXMLFileFolder(filePath);
        }else{
            loadXMLFile(filePath);
        }
    }
    private void loadXMLFile(String fileName){
        File file = new File(fileName);
        if(!file.exists() || !file.canRead())
        {
            //log
            return;
        }
        SAXReader reader = new SAXReader();
        Document document;
        try {
            document = reader.read(fileName);
            List nodes = document.getRootElement().elements(LoadPropertiesFileUtil.getStringValue(Constant.SITE_TEMPLATE_DIV));
            for(Iterator it = nodes.iterator(); it.hasNext();) {
                Element element = (Element) it.next();
                String siteName = element.attributeValue(LoadPropertiesFileUtil.getStringValue(Constant.SITE_SITENAME_ATTR));
                String siteId = element.attributeValue(LoadPropertiesFileUtil.getStringValue(Constant.SITE_SITEID_ATTR));
                String encoding = element.attributeValue(LoadPropertiesFileUtil.getStringValue(Constant.SITE_ENCODING_ATTR));
                String domin = element.attributeValue(LoadPropertiesFileUtil.getStringValue(Constant.SITE_DOMIN_ATTR));

                BookSiteTemplate bookSiteTemplate = new BookSiteTemplate();
                bookSiteTemplate.setSiteId(siteId);
                bookSiteTemplate.setSiteName(siteName);
                bookSiteTemplate.setEncoding(encoding);
                bookSiteTemplate.setDomin(domin);
//                System.out.println(siteId+siteName+encoding+domin);
                Element pagesElem = element.element(LoadPropertiesFileUtil.getStringValue(Constant.SITE_PAGE_ROOT));
                bookSiteTemplate.parserPagesXpath(pagesElem);
                if (this.siteTemplates == null){
                    siteTemplates = new ArrayList<>(5);
                }
                this.siteTemplates.add(bookSiteTemplate);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }
    private void loadXMLFileFolder(String fileDir){
        List<String> paths = PathParserUtil.filePathParser(fileDir);
        if (paths == null || paths.isEmpty()) {
            //log
            return;
        }
        for (int i = 0; i < paths.size(); i++) {
            loadXMLFile(paths.get(i));
        }
    }
}
