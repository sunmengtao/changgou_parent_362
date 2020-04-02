package com.changgou.controller;

import com.changgou.order.feign.CartFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/wcart")
public class CartController {

    @Autowired
    private CartFeign cartFeign;


//    private static final String CART_URL = "http://cart.changgou.com:9111/wcart/list";

    private static final String CART_URL = "http://cart.changgou.com:8001/api/wcart/list";

    @RequestMapping("list")
    public String list(Model model){
        model.addAttribute("result", cartFeign.list().getData());
        return "cart";
    }

    @GetMapping("/add")
    public String add(@RequestParam("skuId") String skuId,@RequestParam("num") Integer num){
        cartFeign.add(skuId,num);
        return "redirect:" + CART_URL;
    }

    @GetMapping("/updateChecked")
    public String updateChecked(@RequestParam("skuId") String skuId,@RequestParam("checked") Boolean checked){
        cartFeign.updateChecked(skuId,checked);
        return "redirect:" + CART_URL;
    }

    public String delete(@RequestParam("skuId") String skuId){
        cartFeign.delete(skuId);
        return "redirect:" + CART_URL;
    }
}
