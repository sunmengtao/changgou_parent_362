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

/**
 * 记录用户请求URL的过滤器
 */
@Component
public class UrlFilter  implements GlobalFilter, Ordered {

    private Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        logger.info("第二个过滤器，记录用户请求的URL地址：{}",path);
        //TODO 将数据存放到数据库表中，为大数据团队提供数据分析的基本数据
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 1; //此处的级别配置数字要要小于2，不然的话网关配置文件中的截断URL的过滤器StripPrefixFilter会优先执行
    }
}
