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
public class IpFilter implements GlobalFilter, Ordered {
        private Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String ip = request.getRemoteAddress().getHostName();
            logger.info("第一个过滤器,记录用户ip:{}",ip);
            //TODO 将数据存放到数据库表中,伟大书记团队提供数据分析的基本数据
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;//过滤器执行的优先级,值越小,优先级越高
    }
}
