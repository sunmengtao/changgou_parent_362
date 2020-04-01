package com.changgou.service;


import org.springframework.http.server.reactive.ServerHttpRequest;

public interface AuthService {


    /**
     * 从请求中获取cookie值，cookie名称为uid
     * @param request
     * @return
     */
    String getJtiFromCookie(ServerHttpRequest request);

    /**
     * 通过JTI短令牌从缓存中查询长令牌
     * @param jti
     * @return
     */
    String getTokenFromRedis(String jti);
}
