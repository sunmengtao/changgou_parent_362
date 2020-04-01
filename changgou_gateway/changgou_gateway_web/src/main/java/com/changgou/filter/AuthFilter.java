package com.changgou.filter;

import com.changgou.service.AuthService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @Autowired
    private AuthService authService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        //1.获取请求和响应
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        //2.获取请求URL,判断是否是登录,如果是则放行
        String path = request.getURI().getPath();
        if ("/api/oauth/interface/login".equals(path) || "/api/oauth/toLogin".equals(path)||"/api/oauth/login".equals(path)){
            return chain.filter(exchange);
        }
        //3.从请求中获取cookie值,判断cookie里面是否为空,如果为空,那么久返回401
        String jti = authService.getJtiFromCookie(request);
        if (StringUtils.isEmpty(jti)){
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        //4.根据UID从REDIS里面找长令牌token,判断token是否为空,如果为空,那么久返回401
        String token = authService.getTokenFromRedis(jti);
        if (StringUtils.isEmpty(token)){
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        //5.处理资源服务器需要的Authorization, 请求头的格式"bearer Token值"
        request.mutate().header("Authorization","bearer " + token);

        //6.执行放行
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
