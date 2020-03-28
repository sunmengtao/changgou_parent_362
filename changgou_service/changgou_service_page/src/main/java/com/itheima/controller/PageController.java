package com.itheima.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.itheima.service.PageService;
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
        return new Result(true, StatusCode.OK,"生成成功");
    }
}
