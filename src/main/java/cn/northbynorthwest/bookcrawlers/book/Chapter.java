package cn.northbynorthwest.bookcrawlers.book;

import lombok.Getter;
import lombok.Setter;

/**
 * “Go Further进无止境” <br>
 * 〈书籍章节类〉
 *
 * @author Luoxun
 * @create 2020/5/7
 * @since 1.0.0
 */
@Getter
@Setter
public class Chapter {
    private String chapterName;
    private String chapterId;
    private String chapterUrl;
    private int chapterWordCounts;
    private String writeTime;
}
