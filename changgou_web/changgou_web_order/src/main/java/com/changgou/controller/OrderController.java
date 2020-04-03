package com.changgou.controller;

import com.alibaba.fastjson.JSON;
import com.changgou.entity.Result;
import com.changgou.order.feign.CartFeign;
import com.changgou.order.feign.OrderFeign;
import com.changgou.order.pojo.Order;
import com.changgou.order.pojo.OrderItem;
import com.changgou.user.feign.AddressFeign;
import com.changgou.user.pojo.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/worder")
public class OrderController {

    @Autowired
    private AddressFeign addressFeign;

    @Autowired
    private CartFeign cartFeign;

    @Autowired
    private OrderFeign orderFeign;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 购物车列表页面地址
     */
    private static final String CART_URL = "http://cart.changgou.com:8001/api/wcart/list";


    /**
     * 支付页面地址
     */
    private static final String PAY_URL = "http://cart.changgou.com:8001/api/wpay/nativePay";


    /**
     * 订单结算页地址
     */
    private static final String READY_URL = "http://web.changgou.com:8001/api/worder/ready";

    /**
     * 点击购物车结算进行跳转的接口
     * @param model
     * @return
     */
    @GetMapping("/ready")
    public String ready(Model model){
        //购物车结算跳转时的业务数据前置判断
        Result result = cartFeign.list();
        Map cartMap = JSON.parseObject(JSON.toJSONString(result.getData()), Map.class);
        if(cartMap.size()==0 || cartMap.get("orderItemList")==null || cartMap.get("totalNum")==null || cartMap.get("totalPrice")==null ){
            logger.error("购物车数据为空！");
            return "redirect:" + CART_URL;
        }

        List<OrderItem> orderItemList = JSON.parseArray(JSON.toJSONString(cartMap.get("orderItemList")), OrderItem.class);
        if(orderItemList.size()==0){
            logger.error("购物车数据为空！");
            return "redirect:" + CART_URL;
        }

        //购物车已经选中的商品数量之和
        Integer totalNum = Integer.valueOf(String.valueOf(cartMap.get("totalNum")));
        //购物车已经选中的商品价格小计之和
        Integer totalPrice = Integer.valueOf(String.valueOf(cartMap.get("totalPrice")));
        if(totalNum==0 || totalPrice==0){
            logger.error("购物车没有选中商品！");
            return "redirect:" + CART_URL;
        }

        //结算页只需要购物车中已经选中的商品
        List<OrderItem> checkedList = new ArrayList<>();
        for (OrderItem orderItem : orderItemList) {
            if(orderItem.isChecked()){
                checkedList.add(orderItem);
            }
        }
        //设置购物车数据
        cartMap.put("orderItemList", checkedList);
        model.addAttribute("cartMap", cartMap);


        //设置收货地址列表数据
        List<Address> addressList = addressFeign.list();
        model.addAttribute("addressList", addressList);

        //设置默认收货地址
        Address defaultAddress = null;
        if(addressList!=null && addressList.size()>0){
            for (Address address : addressList) {
                if("1".equals(address.getIsDefault())){
                    defaultAddress = address;
                }
            }
        }
        model.addAttribute("defaultAddress", defaultAddress);
        return "order";
    }


    @PostMapping("/submit")
    public String submit(Order order){
        boolean sumbit = orderFeign.submit(order);
        if(sumbit){ //如果订单结算页提交结算成功，那么跳转到支付页面
            return "redirect:" + PAY_URL;
        }
        return "redirect:" + READY_URL;
    }
}
