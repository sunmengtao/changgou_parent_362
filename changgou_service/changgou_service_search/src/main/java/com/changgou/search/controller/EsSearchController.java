package com.changgou.search.controller;


import com.changgou.entity.Page;
import com.changgou.search.service.EsSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping("/search")
public class EsSearchController {


    @Autowired
    private EsSearchService esSearchService;

    @GetMapping("/list")
    public String search(@RequestParam Map<String,String> searchMap, Model model){


        Map result = esSearchService.search(searchMap);
        //将查询到的所有商品数据及筛选条件值设置
        model.addAttribute("result", result);


        //将所有查询条件设置回去
        model.addAttribute("searchMap", searchMap);

        //设置分页对象数据
        long total = Long.valueOf(String.valueOf(result.get("total")));
        int pageNum = Integer.valueOf(String.valueOf(result.get("pageNum")));
        Page page = new Page(total, pageNum,  Page.pageSize);
        model.addAttribute("page", page);


        //拼接URL用户回显到页面上，那么页面上的筛选链接都是基于此URL进行请求的
        StringBuffer url = new StringBuffer();
        url.append("/search/list");
        if(searchMap!=null && searchMap.size()>0){
            url.append("?");
            int flag = 0;
            for(String key : searchMap.keySet()){
                if(flag>0){
                    url.append("&");
                }
                //URL回显排除排序相关参数、排除分页码pageNum参数
                if(!"sortField".equalsIgnoreCase(key) && !"sortRule".equalsIgnoreCase(key) && !"pageNum".equalsIgnoreCase(key)){

                    String val = searchMap.get(key);
                    val = val.replace("+",  "%2B");

                    //拼接参数名和参数值
                    url.append(key + "=" + val);
                    flag++;
                }
            }
        }
        model.addAttribute("url", url.toString());

        return "search";
    }
}
