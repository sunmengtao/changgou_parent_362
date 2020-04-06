package com.changgou.pay.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@FeignClient(name = "pay")
@RequestMapping("/pay")
public interface PayFeign {

    @GetMapping("/nativePay")
    public Map nativePay();

}
