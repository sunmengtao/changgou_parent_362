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

    @Test
    public void testCreateJwtExpire(){
        String token = Jwts.builder().signWith(SignatureAlgorithm.HS256, "itheima")
                .setId(UUID.randomUUID().toString()) //JWT的唯一ID
                .setSubject("黑马程序员") //JWT的主题
                .setIssuedAt(new Date())//JWT的生成时间
                .setExpiration(new Date(System.currentTimeMillis()+60*60*1000))
                .compact();
        System.out.println("TOKEN值为：" + token);
    }

    @Test
    public void testCreateJwtCustom(){
        String token = Jwts.builder().signWith(SignatureAlgorithm.HS256, "itheima")
                .setId(UUID.randomUUID().toString()) //JWT的唯一ID
                .setSubject("黑马程序员") //JWT的主题
                .setIssuedAt(new Date())//JWT的生成时间
                .setExpiration(new Date(System.currentTimeMillis()+60*60*1000))
                .claim("username","zhangsan")
                .claim("age",20)
                .claim("address","北京昌平")
                .compact();
        System.out.println("TOKEN值为：" + token);
    }


    /**
     * 解析JWT
     */
    @Test
    public void parseJwt(){
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJiMzYzYWYxZC03OWU5LTQ2YjUtYjRmYy02Mzc3NzliZmU0OGYiLCJzdWIiOiLpu5HpqaznqIvluo_lkZgiLCJpYXQiOjE1ODQ3OTA0MDQsImV4cCI6MTU4NDc5NDAwNCwidXNlcm5hbWUiOiJ6aGFuZ3NhbiIsImFnZSI6MjAsImFkZHJlc3MiOiLljJfkuqzmmIzlubMifQ.y88a-n3Qgf_COsTDnJ0EcQPSr_HbgJW2YzJ2MIuZMXw";
        Jws<Claims> parse = Jwts.parser().setSigningKey("itheima").parseClaimsJws(jwt);
        System.out.println("解析头：" + parse.getHeader());
        Claims body = parse.getBody();
        System.out.println("解析载荷：" + parse.getBody());
        System.out.println("ID" + body.getId());
        System.out.println("SUB" + body.getSubject());
        System.out.println("IAT" + body.getIssuedAt());
        System.out.println("EXP" + body.getExpiration());
        System.out.println("username:"+body.get("username"));
        System.out.println("age:"+body.get("age"));
        System.out.println("address:"+body.get("address"));
        System.out.println("解析签名：" + parse.getSignature());
    }
}
