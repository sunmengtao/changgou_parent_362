package com.changgou.controller;

import com.changgou.pay.feign.PayFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/wpay")
public class PayController {

    @Autowired
    private PayFeign payFeign;

    @GetMapping("/nativePay")
    public String nativePay(Model model){
        Map map = payFeign.nativePay();
        if(map==null||map.get("orderId")==null||map.get("payMoney")==null||map.get("code_url")==null){
            throw new RuntimeException("统一下单出现异常");
        }

        //设置订单号
        model.addAttribute("orderId", map.get("orderId"));
        //设置支付金额
        model.addAttribute("payMoney", Double.valueOf(String.valueOf(map.get("payMoney"))));
        //设置二维码链接
        model.addAttribute("code_url", map.get("code_url"));

        return "wxpay";
    }
}
