package com.changgou;

import com.changgou.config.TokenDecode;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WeChatPayConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableEurekaClient
public class PayApplication {

    public static void main(String[] args) {
        SpringApplication.run(PayApplication.class,args);
    }

    @Bean
    public WXPay wxPay(){
        try {
            return new WXPay( new WeChatPayConfig() );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Bean
    public TokenDecode tokenDecode(){
        return new TokenDecode();
    }
}
