package com.changgou.goods.test;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class EncodingTest {

    @Test
    public void testUrlEncode(){
        try {
            String result = URLEncoder.encode("手机", "UTF-8");
            System.out.println(result);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
