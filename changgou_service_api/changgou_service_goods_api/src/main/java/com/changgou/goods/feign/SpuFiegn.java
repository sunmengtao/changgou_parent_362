package com.changgou.goods.feign;

import com.changgou.goods.pojo.Spu;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "goods")
public interface SpuFiegn {

    @GetMapping("/{id}")
    public Spu findById(@PathVariable("id") String id);
}
