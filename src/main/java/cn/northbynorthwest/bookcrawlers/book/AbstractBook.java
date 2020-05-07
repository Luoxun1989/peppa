package cn.northbynorthwest.bookcrawlers.book;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * “Go Further进无止境” <br>
 * 〈书籍抽象类〉
 *
 * @author Luoxun
 * @create 2020/5/7
 * @since 1.0.0
 */
@Setter
@Getter
public abstract class AbstractBook {
    public String bookName;
    public String author;
    public String pseudonym;
    public int wordCounts;
    public int chapterCounts;
    public float price;
    public List<Chapter> Chapters;

    public abstract boolean equals(Object obj);
}
