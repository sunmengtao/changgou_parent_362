package com.changgou.goods.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "goods")
@RequestMapping("/sku")
public interface SkuFiegn {
}
