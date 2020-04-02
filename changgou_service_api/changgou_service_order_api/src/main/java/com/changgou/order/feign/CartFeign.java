package com.changgou.order.feign;

import com.changgou.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "order")
@RequestMapping("/cart")
public interface CartFeign {


    @GetMapping("/add")
    public Result add(@RequestParam("skuId") String skuId, @RequestParam("num") Integer num);

    @GetMapping("/list")
    public Result list();

    @GetMapping("/updateChecked")
    public Result updateChecked(@RequestParam("skuId") String skuId,@RequestParam("checked") Boolean checked);

    @GetMapping("/delete")
    public Result delete(@RequestParam("skuId") String skuId);
}
