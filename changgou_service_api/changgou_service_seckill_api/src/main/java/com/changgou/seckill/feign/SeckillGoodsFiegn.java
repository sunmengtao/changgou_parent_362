package com.changgou.seckill.feign;

import com.changgou.seckill.pojo.SeckillGoods;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "seckill")
@RequestMapping("/seckillgoods")
public interface SeckillGoodsFiegn {

    @GetMapping("/list")
    public List<SeckillGoods> list(@RequestParam("time") String time);
}
