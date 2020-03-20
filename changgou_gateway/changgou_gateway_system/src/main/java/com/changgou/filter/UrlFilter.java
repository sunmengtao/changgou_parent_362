package com.changgou.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
@Component
public class UrlFilter implements GlobalFilter, Ordered {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        logger.info("第二个过滤器,记录用户请求的路径 :{}",path);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 1;//此处的级别配置数字要小于2,要不然的话网管中的阶段URL的过滤器StripPrefixFilter会优先执行
    }
}
