package com.changgou.pay.test;

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


    /**
     * 测试： 微信支付 - 统一下单接口
     * 使用场景：为了获取支付二维码链接
     *
     * 订单Id：3EZ85IP1iOkyUieGB86U
     * 统一下单响应结果：
     * {
     * nonce_str=rYH2Fa40p2c9rk1a,
     * code_url=weixin://wxpay/bizpayurl?pr=zpIPCzE,   //二维码链接,用于生成支付二维码，然后提供给用户进行扫码支付
     * appid=wx8397f8696b538317,
     * sign=8B92A2F158AF03AB3988301F3F0A238A96EF5E9E195E1EBCF77236DD7AF95203,
     * trade_type=NATIVE,  //NATIVE -Native支付， 就是二维码支付的意思
     * return_msg=OK,
     * result_code=SUCCESS,  //	 SUCCESS/FAIL 此字段是通信标识，非交易标识，交易是否成功需要查看result_code来判断
     * mch_id=1473426802,
     * return_code=SUCCESS,  // 业务处理结果标识
     * prepay_id=wx051018225447810b78f6753a1555313400
     * }
     */
    @Test
    public void testUnifiedOrder(){
        RandomValueStringGenerator generator = new RandomValueStringGenerator(20);
        String orderId = generator.generate();
        System.out.println("订单Id：" + orderId);

        Map<String,String> reqData = new HashMap<>();
        reqData.put("body", "畅购测试商品");//商品描述
        reqData.put("out_trade_no", orderId);//商户订单号，在此表示畅购订单号
        reqData.put("total_fee", "1");//支付金额，单位是分
        reqData.put("spbill_create_ip", "127.0.0.1");//客户端所在的网络IP
        reqData.put("notify_url", "http://localhost/");//商户的回调地址，在此表示畅购给微信提供的回调地址，用户更新用户支付的订单状态
        reqData.put("trade_type", "NATIVE");//交易类型

        try {
            Map<String, String> respMap = wxPay.unifiedOrder(reqData);
            System.out.println("统一下单响应结果："  + respMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 测试： 微信支付 - 订单查询
     * 使用场景：当商户需要主动获取微信支付订单交易状态的时候
     *
     * 微信支付查询响应结果：
     * {
     * nonce_str=tVoE5sJzza2gYBBC,
     * device_info=,
     * out_trade_no=3EZ85IP1iOkyUieGB86U,
     * trade_state=NOTPAY,
     * appid=wx8397f8696b538317,
     * total_fee=1,
     * sign=F2ADFBAE2558BA4DCE68133EBF9CCBA8D420F61BDDE36CADF8FCDEAD54B0D79E,
     * trade_state_desc=订单未支付,
     * return_msg=OK,
     * result_code=SUCCESS,
     * mch_id=1473426802,
     * return_code=SUCCESS
     * }
     *
     *
     * 微信支付查询响应结果：
     * {
     * nonce_str=ohiB15S9CHZ6QgJY,
     * out_trade_no=3EZ85IP1iOkyUieGB86U,
     * trade_state=CLOSED,
     * appid=wx8397f8696b538317,
     * sign=5A8CAFB786B516E8F1244412EA8C5C57B6CA0E93151558D1648CDBB6809FD331,
     * trade_state_desc=订单已关闭,
     * return_msg=OK,
     * result_code=SUCCESS,
     * attach=,
     * mch_id=1473426802,
     * return_code=SUCCESS
     * }
     */
    @Test
    public void testQueryOrder(){

        Map<String,String> reqData = new HashMap<>();
        reqData.put("out_trade_no", "3EZ85IP1iOkyUieGB86U"); //商户订单号，在此表示畅购订单号

        try {
            Map<String, String> respMap = wxPay.orderQuery(reqData);
            System.out.println("微信支付查询响应结果：" + respMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 测试： 微信支付 - 订单关闭
     * 使用场景： 当用户主动去掉订单 或者 订单超时未支付
     *
     * 微信支付关闭订单响应结果：
     * {
     * nonce_str=jqC0ip4kLh5echDj,
     * appid=wx8397f8696b538317,
     * sign=176C77651D50D7673D6BB09678ACACAE6B3AA55A473ADFF7321BEC411FCC2E7E,
     * return_msg=OK,
     * result_code=SUCCESS,
     * mch_id=1473426802,
     * sub_mch_id=,
     * return_code=SUCCESS
     * }
     */
    @Test
    public void testCloseOrder(){

        Map<String,String> reqData = new HashMap<>();
        reqData.put("out_trade_no", "3EZ85IP1iOkyUieGB86U"); //商户订单号，在此表示畅购订单号

        try {
            Map<String, String> respMap = wxPay.closeOrder(reqData);
            System.out.println("微信支付关闭订单响应结果：" + respMap);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
