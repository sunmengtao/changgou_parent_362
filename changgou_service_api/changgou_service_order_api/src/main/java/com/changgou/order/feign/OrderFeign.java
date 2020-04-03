package com.changgou.order.feign;

import com.changgou.order.pojo.Order;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "order")
@RequestMapping("/order")
public interface OrderFeign {

    @PostMapping("/submit")
    public Boolean submit(@RequestBody Order order);
}
