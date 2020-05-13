package cn.northbynorthwest.pipeline;

import cn.northbynorthwest.bookcrawlers.book.Chapter;
import cn.northbynorthwest.bookcrawlers.book.ElectronicBook;
import cn.northbynorthwest.template.PageAttributeEnum;
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
import java.util.concurrent.locks.ReentrantLock;

/**
 * “Go Further进无止境” <br>
 * 〈〉
 *
 * @author Luoxun
 * @create 2020/4/30
 * @since 1.0.0
 */
public class EBookFilePipeline extends FilePersistentBase implements Pipeline {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    private ReentrantLock reentrantLock = new ReentrantLock();
    public EBookFilePipeline() {
        this.setPath("/data/webmagic/");
    }

    public EBookFilePipeline(String path) {
        this.setPath(path);

    }

    @Override
    public void process(ResultItems resultItems, Task task) {

        String path = this.path + PATH_SEPERATOR + task.getUUID() + PATH_SEPERATOR + format.format(new Date());
        this.getFile(path);
        try {
            //追加模式写入数据
            PrintWriter printContentWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(
                    this.getFile(path + PATH_SEPERATOR + PageAttributeEnum.CONTENTPAGE.name()),true), "UTF-8"));
            PrintWriter printChapterWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(
                    this.getFile(path + PATH_SEPERATOR + PageAttributeEnum.CHAPTERPAGE.name()),true), "UTF-8"));
            Iterator var5 = resultItems.getAll().entrySet().iterator();
            while (true) {
                while (var5.hasNext()) {
                    Map.Entry<String, Object> entry = (Map.Entry) var5.next();
                    if (entry.getValue() instanceof ElectronicBook) {
                        try {
                            reentrantLock.lock();
                            printContentWriter.println(entry.getValue().toString());
                        }finally {
                            reentrantLock.unlock();
                        }
                    } else if (entry.getValue() instanceof Chapter) {
                        try {
                            reentrantLock.lock();
                            printChapterWriter.println(entry.getValue().toString());
                        }finally {
                            reentrantLock.unlock();
                        }
                    } else {
                        //do Other
                    }
                }
                printContentWriter.close();
                printChapterWriter.close();
                break;
            }
        } catch (IOException var10) {
            this.logger.warn("write file error", var10);
        }

    }
}
