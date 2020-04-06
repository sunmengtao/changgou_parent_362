package com.changgou.pay.controller;

import com.changgou.config.TokenDecode;
import com.changgou.pay.service.PayService;
import com.github.wxpay.sdk.WXPayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Autowired
    private TokenDecode tokenDecode;

    @Autowired
    private PayService payService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping("/nativePay")
    public Map nativePay(){
        String username = tokenDecode.getUserInfo().get("username");
        return payService.nativePay(username);
    }

    @PostMapping("/notify")
    public String payNofity(HttpServletRequest request){
        logger.info("微信支付回调了........");

        Map<String, String> respMap = new HashMap<>();
        respMap.put("return_code","SUCCESS");
        respMap.put("return_msg","OK");
        try {
            return WXPayUtil.mapToXml(respMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "回调完毕";
    }
}
