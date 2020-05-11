package cn.northbynorthwest.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class StringOperateUtilTest {

    @Test
    public void extractIdFromUrl() {
        assertEquals("1012902516",StringOperateUtil.extractIdFromUrl("https://book.qidian.com/info/1012902516"));
        assertEquals("1012902516",StringOperateUtil.extractIdFromUrl("https://book.qidian.com/info/1012902516.html"));
    }

    @Test
    public void match() {
        assertEquals(false,StringOperateUtil.match("https://book.qidian.com/info/1012902516",
                "https://book.qidian.com/info/\\d+#Catalog"));
        assertEquals(false,StringOperateUtil.match("https://book.qidian.com/info/1012902516#Catalog",
                "https://book.qidian.com/info/\\d+"));
        assertEquals(true,StringOperateUtil.match("https://book.qidian.com/info/1012902516",
                "https://book.qidian.com/info/\\d+"));
        assertEquals(true,StringOperateUtil.match("https://book.qidian.com/info/1012902516#Catalog",
                "https://book.qidian.com/info/\\d+#Catalog"));
    }
}