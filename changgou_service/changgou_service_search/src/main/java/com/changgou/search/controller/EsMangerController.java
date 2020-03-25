package com.changgou.search.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.search.service.EsManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manage")
public class EsMangerController {

    @Autowired
    private EsManagerService esManagerService;

    @RequestMapping("/deleteIndexAndMapping")
    public Result deleteIndexAndMapping(){
        esManagerService.deleteIndexAndMapping();
        return new Result(true, StatusCode.OK,"删除索引和映射成功");
    }

    @PostMapping("/createIndexAndMapping")
    public Result createIndexAndMapping(){
        esManagerService.createIndexAndMapping();
        return new Result(true,StatusCode.OK,"创建索引库和映射关系成功");
    }

    @PostMapping("/importBySpuId/{spuId}")
    public Result importBySpuId(@PathVariable("spuId") String spuId){
        esManagerService.importBySpuId(spuId);
        return new Result(true,StatusCode.OK,"根据SPUID导入数据到ES成功");
    }

    @PostMapping("/importAll")
    public Result importAll(){
        esManagerService.importAll();
        return new Result(true,StatusCode.OK,"导入全部商品数据到ES成功");
    }

    @PostMapping("/deleteBySpuId/{spuId}")
    public Result deleteBySpuId(@PathVariable("spuId")String spuId){
        esManagerService.deleteBySpuId(spuId);
        return new Result(true,StatusCode.OK,"根据spuId从ES删除SKU数据成功");
    }
}
