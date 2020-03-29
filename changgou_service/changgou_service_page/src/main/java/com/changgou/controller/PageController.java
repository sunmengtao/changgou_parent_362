package com.changgou.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/page")
public class PageController {

    @Autowired
    private PageService pageService;

    @PostMapping("/createPageHtml/{spuId}")
    @ResponseBody
    public Result createPageHtml(@PathVariable("spuId") String spuId){
        pageService.createPageHtml(spuId);
        return new Result(true, StatusCode.OK, "生成成功");
    }


    /**
     * 通过controller跳转访问templates下的静态页面
     * @param spuId
     * @return
     */
    @GetMapping("/detail/{spuId}")
    public String detail(@PathVariable("spuId") String spuId){

        return "detail/" + spuId;
    }


    @PostMapping("/deletePageHtml/{spuId}")
    @ResponseBody
    public Result deletePageHtml(@PathVariable("spuId") String spuId){
        pageService.deletePageHtml(spuId);
        return new Result(true, StatusCode.OK, "删除成功");
    }
}
