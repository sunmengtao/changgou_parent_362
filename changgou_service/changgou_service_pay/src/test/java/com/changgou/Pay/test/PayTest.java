package com.changgou.Pay.test;

import com.changgou.PayApplication;
import com.github.wxpay.sdk.WXPay;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = PayApplication.class)
@RunWith(SpringRunner.class)
public class PayTest {

    @Autowired
    private WXPay wxPay;


    /*
     * 订单id:vYDnWj2fmJvCx62nt9tq
     * nonce_str=TZAsqmsqL5qgRlZn,
     * code_url=weixin://wxpay/bizpayurl?pr=SWbfQhB,
     * appid=wx8397f8696b538317,
     * sign=30255FF3A58460E30A6EED8EB931522A69916392D7582EB2DA7C65C5D6BB044D,
     * trade_type=NATIVE,
     * return_msg=OK,
     * result_code=SUCCESS,
     * mch_id=1473426802,
     * return_code=SUCCESS,
     * prepay_id=wx05151426234307e0c105fdf21049137600}
     * */
    @Test
    public void testUnifiedOrder(){

        RandomValueStringGenerator generator = new RandomValueStringGenerator(20);
        String orderId = generator.generate();
        System.out.println("订单Id: " + orderId);

        Map<String, String> reqData = new HashMap<>();
        reqData.put("body","畅购测试商品");
        reqData.put("out_trade_no",orderId);
        reqData.put("total_fee","1");
        reqData.put("spbill_create_ip","127.0.0.1");
        reqData.put("notify_url","http://localhost/");
        reqData.put("trade_type","NATIVE");

        try {
            Map<String, String> respMap = wxPay.unifiedOrder(reqData);
            System.out.println("统一下单响应结果: " + respMap);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*
     * {nonce_str=r6GMPCAMTCkcJSXn,
     * device_info=,
     * out_trade_no=vYDnWj2fmJvCx62nt9tq,
     * trade_state=NOTPAY,
     * appid=wx8397f8696b538317,
     * total_fee=1,
     * sign=2F51968902C88B542B12565280FF616BF803274B921B5155C577D17998BF8831,
     * trade_state_desc=订单未支付,
     * return_msg=OK,
     * result_code=SUCCESS,
     * mch_id=1473426802,
     * return_code=SUCCESS}
     * */
    @Test
    public void testQueryOrder(){

        Map<String, String> reqData = new HashMap<>();
        reqData.put("out_trade_no", "vYDnWj2fmJvCx62nt9tq");

        try {
            Map<String, String> respMap = wxPay.orderQuery(reqData);
            System.out.println("微信支付查询响应结果: " + respMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*
     * {nonce_str=kffvWRy83EQvOMcn,
     * appid=wx8397f8696b538317,
     * sign=784B64126ADD1AA60ADEA334217C6D9D4252B59DCFC1328E594707B249D41A7C,
     * return_msg=OK,
     * result_code=SUCCESS,
     * mch_id=1473426802,
     * sub_mch_id=,
     * return_code=SUCCESS}
     *
     *
     * 微信支付查询响应结果:{nonce_str=FW2lzU0zyVFdsBfz,
     * out_trade_no=vYDnWj2fmJvCx62nt9tq,
     * trade_state=CLOSED,
     * appid=wx8397f8696b538317,
     * sign=4C773CABC995A10BFAD11DCFDCE20F560647ED6477E2394648DD7176EA815FB1,
     * trade_state_desc=订单已关闭,
     * return_msg=OK,
     * result_code=SUCCESS,
     * attach=,
     * mch_id=1473426802,
     * return_code=SUCCESS}
     * */
    @Test
    public void testCloseOrder(){

        Map<String, String> reqData = new HashMap<>();
        reqData.put("out_trade_no", "vYDnWj2fmJvCx62nt9tq");

        try {
            Map<String, String> respMap = wxPay.closeOrder(reqData);
            System.out.println("微信支付关闭订单响应结果: " + respMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
