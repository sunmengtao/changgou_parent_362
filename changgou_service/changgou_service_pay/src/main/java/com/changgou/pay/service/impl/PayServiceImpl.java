package com.changgou.pay.service.impl;

import com.changgou.entity.Constants;
import com.changgou.order.pojo.Order;
import com.changgou.pay.service.PayService;
import com.github.wxpay.sdk.WXPay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
@Service
public class PayServiceImpl implements PayService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private WXPay wxPay;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${notifyUrl}")
    private String notifyUrl;

    @Override
    public Map nativePay(String username) {

        Map returnMap = new HashMap<>();

        //1.根据用户名在缓存中获取待支付订单信息并判断
        Order order = (Order) redisTemplate.boundValueOps(Constants.REDIS_ORDER_PAY + username).get();
        if (order==null){
            logger.error("待支付订单不存在! username:{}" + username);
            throw new RuntimeException("待支付订单不存在!");
        }
        //2.根据提交的订单信息调用微信统一下单支付
        Map<String, String> reqData = new HashMap<>();
        reqData.put("body","畅购测试商品");
        reqData.put("out_trade_no",order.getId());
        reqData.put("total_fee","1");
        reqData.put("spbill_create_ip","127.0.0.1");
        reqData.put("notify_url",notifyUrl);
        reqData.put("trade_type","NATIVE");

        try {
            Map<String, String> respMap = wxPay.unifiedOrder(reqData);
            //3.从微信统一下单支付里获取二维码链接
            String code_url = respMap.get("code_url");
            returnMap.put("code_url",code_url);
            returnMap.put("orderId",order.getId());
            returnMap.put("payMoney",order.getPayMoney());
        } catch (Exception e) {
            logger.error("统一下单失败! username:{}" + username,e);
        }
        return returnMap;

    }
}
