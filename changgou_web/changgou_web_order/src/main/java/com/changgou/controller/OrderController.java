package com.changgou.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/worder")
public class OrderController {


    @GetMapping("/ready")
    public String ready(Model model){
        //设置购物车数据

        //设置收货地址列表数据

        //设置默认收货地址

        return "order";
    }
}
