package cn.northbynorthwest.pipeline;

import cn.northbynorthwest.bookcrawlers.book.ElectronicBook;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.utils.FilePersistentBase;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * “Go Further进无止境” <br>
 * 〈〉
 *
 * @author Luoxun
 * @create 2020/4/30
 * @since 1.0.0
 */
public class FilePipeline extends FilePersistentBase implements Pipeline {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

    public FilePipeline() {
        this.setPath("/data/webmagic/");
    }

    public FilePipeline(String path) {
        this.setPath(path);

    }

    @Override
    public void process(ResultItems resultItems, Task task) {

        String path = this.path + PATH_SEPERATOR + task.getUUID() + PATH_SEPERATOR + format.format(new Date());
        try {
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.getFile(path)), "UTF-8"));
            //+ DigestUtils.md5Hex(resultItems.getRequest().getUrl()
            Iterator var5 = resultItems.getAll().entrySet().iterator();
            while (true) {
                while (var5.hasNext()) {
                    Map.Entry<String, Object> entry = (Map.Entry) var5.next();
                    if (entry.getValue() instanceof ElectronicBook){
                        printWriter.println(entry.getValue().toString());
                    }
                }
                printWriter.close();
                break;
            }
        } catch (IOException var10) {
            this.logger.warn("write file error", var10);
        }

    }
}
