package com.changgou.goods.feign;

import com.changgou.goods.pojo.Sku;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(name = "goods")
@RequestMapping("/sku")
public interface SkuFiegn {

    @GetMapping("/findBySpuId/{spuId}")
    public List<Sku> findBySpuId(@PathVariable("spuId") String spuId);

    @GetMapping
    public List<Sku> findAll();
}
