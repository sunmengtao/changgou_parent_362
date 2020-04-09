package com.changgou.seckill.controller;

import com.changgou.util.DateUtil;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/wseckillgoods")
public class WebSeckGoodsController {

    @GetMapping("/toIndex")
    public String toIndex(){
        return "seckill-index";
    }
    @GetMapping("/timeMenus")
    @ResponseBody
    public List<String> timeMenus(){
        //获取某个时间段
        List<Date> dateMenus = DateUtil.getDateMenus();
        List<String> dateList = new ArrayList<>();
        for (Date dateMenu : dateMenus) {
            String dateFormat = DateFormatUtils.format(dateMenu, "yyyy-MM-dd HH:mm:ss");
            dateList.add(dateFormat);
        }
        return dateList;
    }
}
