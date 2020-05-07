package cn.northbynorthwest.bookcrawlers.book;

import lombok.Getter;
import lombok.Setter;

/**
 * “Go Further进无止境” <br>
 * 〈纸书类〉
 *
 * @author Luoxun
 * @create 2020/4/30
 * @since 1.0.0
 */
@Setter
@Getter
public class PaperBook extends AbstractBook{
    private String publisher;
    private String provider;
    private String publishDate;
    private String edition;
    private String ISBN;

    @Override
    public boolean equals(Object obj) {
        //TODO
        return false;
    }
}
