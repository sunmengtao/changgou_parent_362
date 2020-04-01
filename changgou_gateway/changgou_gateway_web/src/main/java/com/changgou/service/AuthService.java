package com.changgou.service;

import org.springframework.http.server.reactive.ServerHttpRequest;

public interface AuthService {

    String getJtiFromCookie(ServerHttpRequest request);

    String getTokenFromRedis(String jti);
}
