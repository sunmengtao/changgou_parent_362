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
public class EsManagerController {

    @Autowired
    private EsManagerService esManagerService;

    @PostMapping("/deleteIndexAndMapping")
    public Result deleteIndexAndMapping(){
        esManagerService.deleteIndexAndMapping();
        return new Result(true, StatusCode.OK, "删除索引库和映射关系成功！");
    }

    @PostMapping("/createIndexAndMappings")
    public Result createIndexAndMapping(){
        esManagerService.createIndexAndMapping();
        return new Result(true, StatusCode.OK,"创建索引库和映射关系成功");
    }

    @PostMapping("/importBySpuId/{spuId}")
    public Result importBySpuId(@PathVariable("spuId") String spuId){
        esManagerService.importBySpuId(spuId);
        return new Result(true, StatusCode.OK, "根据SPUID导入数据到ES成功");
    }
}
