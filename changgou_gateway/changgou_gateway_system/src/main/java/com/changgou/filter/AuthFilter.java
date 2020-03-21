package com.changgou.filter;

import com.changgou.util.JwtUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 用户请求鉴权的过滤器
 */
@Component
public class AuthFilter implements GlobalFilter, Ordered {


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();//获取请求
        ServerHttpResponse response = exchange.getResponse();//获取响应

        //1、获取用户请求的URL路径
        String path = request.getURI().getPath();

        //2、根据路径判断是否是登录，如果是登录，则放行
        if(path.equals("/system/admin/login")){
            return chain.filter(exchange);
        }

        //3、如果不是登录则继续判断，从请求头获取token值
        String token = request.getHeaders().getFirst("token");

        //4、判断TOKEN值是否为空，如果为空，那么返回用户401
        if(StringUtils.isEmpty(token)){
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        //5、 如果TOKEN不为空，那么解析TOKEN
        try {
            JwtUtil.parseJWT(token);
        } catch (Exception e) {
            //6、 如果解析JTW出现异常那么返回用户401， 解析出错的原因（JWT的头、载荷、签名造假，或者JWT时间过期，获取JWT时间还未到）
            e.printStackTrace();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        //7、如果解析成功，直接放行
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
