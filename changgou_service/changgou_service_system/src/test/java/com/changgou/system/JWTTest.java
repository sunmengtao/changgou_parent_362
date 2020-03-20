package com.changgou.system;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Test;

import java.util.Date;
import java.util.UUID;

/**
 * JWT单元测试
 */
public class JWTTest {


    /**
     * 生成JWT测试
     * eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJlODA4NTU2ZS02NDA1LTQyOGQtOTY1My1iZjE4OTc0ZjFiZGMiLCJzdWIiOiLpu5HpqaznqIvluo_lkZgiLCJpYXQiOjE1ODQ2ODcwNTh9.ts4lehpSWl27IMJC3IYNCVGbuy41DZN_0VZ1j6lIA8U
     */
    @Test
    public void testCreateJwt(){
        String token = Jwts.builder().signWith(SignatureAlgorithm.HS256, "itheima")
                .setId(UUID.randomUUID().toString()) //JWT的唯一ID
                .setSubject("黑马程序员") //JWT的主题
                .setIssuedAt(new Date())//JWT的生成时间
                .compact();
        System.out.println("TOKEN值为：" + token);
    }


    /**
     * 解析JWT
     */
    @Test
    public void parseJwt(){
        String jwt = "eyJhbGciOiJIUzI1NiJ9.1eyJqdGkiOiJlODA4NTU2ZS02NDA1LTQyOGQtOTY1My1iZjE4OTc0ZjFiZGMiLCJzdWIiOiLpu5HpqaznqIvluo_lkZgiLCJpYXQiOjE1ODQ2ODcwNTh9.ts4lehpSWl27IMJC3IYNCVGbuy41DZN_0VZ1j6lIA8U";
        Jws<Claims> parse = Jwts.parser().setSigningKey("itheima").parseClaimsJws(jwt);
        System.out.println("解析头：" + parse.getHeader());
        System.out.println("解析头：" + parse.getBody());
        System.out.println("解析头：" + parse.getSignature());
    }
}
