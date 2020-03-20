package com.changgou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@SpringBootApplication
@EnableEurekaClient
public class SystemGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(SystemGatewayApplication.class,args);
    }

    @Bean
    public KeyResolver ipKeyResolver(){

        return new KeyResolver() {
            @Override
            public Mono<String> resolve(ServerWebExchange exchange) {
                //设置限流的维度，根据什么限流：根据用户IP来限流
                return Mono.just(exchange.getRequest().getRemoteAddress().getHostName());
            }
        };
    }
}
