package com.changgou.seckill.controller;

import com.changgou.seckill.feign.SeckillGoodsFiegn;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.util.DateUtil;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/wseckillgoods")
public class WebSeckGoodsController {

    @Autowired
    private SeckillGoodsFiegn seckillGoodsFiegn;
    
    @GetMapping("/toIndex")
    public String toIndex(){
        return "seckill-index";
    }

    @GetMapping("/timeMenus")
    @ResponseBody
    public List<String> timeMenus(){
        //获取5个时间段
        List<Date> dateMenus = DateUtil.getDateMenus();
        //yyyy-MM-dd HH:mm:ss
        List<String> dateList = new ArrayList<>();
        for (Date dateMenu : dateMenus) {
            String dateFormat = DateFormatUtils.format(dateMenu, "yyyy-MM-dd HH:mm:ss");//转成前端所需要的的时间格式
            dateList.add(dateFormat);
        }
        return dateList;
    }


    @GetMapping("/list")
    @ResponseBody
    public List<SeckillGoods> list(@RequestParam("time") String time){
        //页面传过来的时间格式是yyyy-MM-dd HH:mm:ss
        try {
            Date date = DateUtils.parseDate(time, new String[]{"yyyy-MM-dd HH:mm:ss"});
            String dateStr = DateFormatUtils.format(date, "yyyyMMddHH");
            return seckillGoodsFiegn.list(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
