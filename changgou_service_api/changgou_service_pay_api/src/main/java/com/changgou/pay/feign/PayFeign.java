package com.changgou.pay.feign;

import com.changgou.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "pay")
@RequestMapping("/pay")
public interface PayFeign {

    @GetMapping("/nativePay")
    public Map nativePay();

    @GetMapping("/queryOrder")
    public Map queryOrder(@RequestParam("orderId") String orderId);

    @GetMapping("/closeOrder")
    public Result closeOrder(@RequestParam("orderId")String orderId);
}
