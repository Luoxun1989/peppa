package cn.northbynorthwest.bookcrawlers.book;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * “Go Further进无止境” <br>
 * 〈电子书抽象类〉
 *
 * @author Luoxun
 * @create 2020/4/30
 * @since 1.0.0
 */
@Getter
@Setter
public class ElectronicBook extends AbstractBook {
    private String siteId;
    private String bookId;
    private String bookUrl;
    private String siteName;
    // 是否更新完结
    private boolean isFinish;
    // 书籍标签
    private String tag;
    // 书籍所属大类
    private String className;
    // 书籍内容简介
    private String introduction;
    // 书籍章节列表页url
    private String chapterListUrl;

    public String toString() {
        return String.format("%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s", siteId, siteName, bookName, author, pseudonym,
                bookId, bookUrl, isFinish, wordCounts, price, tag, className, chapterListUrl,introduction);
    }
    public String toMainInfoString() {
        return String.format("%s|%s|%s|%s|%s|%s|%s", siteId, siteName, bookName, author, pseudonym,
                bookId, bookUrl);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ElectronicBook)) {
            return false;
        }
        if (obj != null && obj.getClass() == this.getClass()) {
            ElectronicBook ebook = (ElectronicBook) obj;
            if (ebook.getBookName() == null || bookName == null) {
                return false;
            } else {
                return siteId.equalsIgnoreCase(ebook.siteId) && bookId.equalsIgnoreCase(ebook.bookId) &&
                        bookName.equalsIgnoreCase(ebook.getBookName()) && pseudonym.equalsIgnoreCase(ebook.getPseudonym());
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((siteId == null) ? 0 : siteId.hashCode());
        result = prime * result + ((bookId == null) ? 0 : bookId.hashCode());
        result = prime * result + ((bookName == null) ? 0 : bookName.hashCode());
        result = prime * result + ((pseudonym == null) ? 0 : pseudonym.hashCode());
        return result;
    }
}
