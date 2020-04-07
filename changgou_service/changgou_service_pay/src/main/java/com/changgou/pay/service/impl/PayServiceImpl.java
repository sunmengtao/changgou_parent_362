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

    @Value("${notifyUrl}")
    private String notifyUrl;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Map nativePay(String username) {
        Map returnMap = new HashMap<>();
        //1.根据用户名从缓存中获取待支付订单信息并判断
        Order order = (Order) redisTemplate.boundValueOps(Constants.REDIS_ORDER_PAY + username).get();
        if(order==null){
            logger.error("待支付订单不存在！username:{}",username);
            throw new RuntimeException("待支付订单不存在！");
        }

        //2.根据提交的订单信息调用微信统一下单支付
        Map<String,String> reqData = new HashMap<>();
        reqData.put("body", "畅购测试商品"); //商品描述
        reqData.put("out_trade_no", order.getId()); //畅购订单号，也称商户订单号
        reqData.put("total_fee", "1");//支付金额，这里写1分是方便我们自己做测试使用，实际生产中这里应该是订单的支付金额
        reqData.put("spbill_create_ip", "127.0.0.1"); //客户端IP地址，这里我们写本地IP方便测试，实际生产者里应该是获取的用户客户端真实IP
        reqData.put("notify_url", notifyUrl); //临时写个假接口
        reqData.put("trade_type", "NATIVE"); //交易类型，NATIVE -Native支付
        try {
            Map<String, String> respMap = wxPay.unifiedOrder(reqData);
            //3.从微信统一下单支付里获取二维码链接
            String code_url = respMap.get("code_url");
            returnMap.put("code_url", code_url);
            returnMap.put("orderId", order.getId()); //订单号
            returnMap.put("payMoney",  order.getPayMoney()); //支付金额
        } catch (Exception e) {
            logger.error("统一下单失败！username:{}",username, e);
        }
        return returnMap;
    }


    @Override
    public Map queryOrder(String orderId) {
        Map<String,String> queryMap = new HashMap<>();
        Map<String,String> reqData = new HashMap<>();
        reqData.put("out_trade_no", orderId);
        try {
            Map<String, String> respMap = wxPay.orderQuery(reqData);
            String trade_state = respMap.get("trade_state");//交易状态
            String time_end = respMap.get("time_end"); //交易完成时间
            String transaction_id = respMap.get("transaction_id"); //微信支付订单号
            queryMap.put("trade_state", trade_state);
            queryMap.put("time_end", time_end);
            queryMap.put("transaction_id", transaction_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return queryMap;
    }


    @Override
    public void closeOrder(String orderId) {
        Map<String,String> reqData = new HashMap<>();
        reqData.put("out_trade_no", orderId);
        try {
            wxPay.closeOrder(reqData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
