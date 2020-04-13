package com.changgou.seckill.controller;

import com.changgou.seckill.config.TokenDecode;
import com.changgou.seckill.service.SeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seckillorder")
public class SeckillOrderControlller {

    @Autowired
    private SeckillOrderService seckillOrderService;

    @Autowired
    private TokenDecode tokenDecode;

    @GetMapping("/add")
    public Boolean add(@RequestParam("time") String time,@RequestParam("id") Long id ){
        String username = tokenDecode.getUserInfo().get("username");
        return seckillOrderService.add(username,time,id);
    }
}
