package com.changgou.system;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Test;

import java.util.Date;
import java.util.UUID;

public class JWTTest {


    @Test
    public void testCreateJwt(){
       String token = Jwts.builder().signWith(SignatureAlgorithm.HS256,"itheima")
                .setId(UUID.randomUUID().toString())
                .setSubject("黑马程序员")
                .setIssuedAt(new Date())
                .compact();
        System.out.println("TOKEN值为:" + token);
    }

    @Test
    public void parseJwt(){
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIwNzBkNDcyYi1jOGIxLTQzNTktODE1NC1hZTM3OGRkMjJhYzAiLCJzdWIiOiLpu5HpqaznqIvluo_lkZgiLCJpYXQiOjE1ODQ3MTU4Mzd9.jPC2BRQ4vT5tVOFovatpWXIajpY5nG9eLTow6t3wJrA";
        Jws<Claims> parse = Jwts.parser().setSigningKey("itheima").parseClaimsJws(jwt);
        System.out.println("解析头:" + parse.getHeader());
        System.out.println("解析头:" + parse.getBody());
        System.out.println("解析头:" + parse.getSignature());
    }
}
