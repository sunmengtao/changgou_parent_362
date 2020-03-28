package com.changgou.search.controller;


import com.changgou.entity.Page;
import com.changgou.search.service.EsSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Controller
@RequestMapping("/search")
public class EsSearchController {


    @Autowired
    private EsSearchService esSearchService;


    @GetMapping("/list")
    public String search(@RequestParam Map<String,String> searchMap, Model model){
        Map result = esSearchService.search(searchMap);
        model.addAttribute("result",result);
        model.addAttribute("searchMap",searchMap);

        Long total = Long.valueOf(String.valueOf(result.get("total")));
        int pageNum = Integer.valueOf(String.valueOf(result.get("pageNum")));
        Page page = new Page(total, pageNum, Page.pageSize);
        model.addAttribute("page",page);

        StringBuffer url = new StringBuffer();
        url.append("/search/list");
        if (searchMap!=null && searchMap.size()>0){
            url.append("?");
            int flag = 0;
            for (String key : searchMap.keySet()) {
                if (flag>0){
                    url.append("&");
                }
                if (!"sortField".equalsIgnoreCase(key) && !"sortRule".equalsIgnoreCase(key) && !"pageNum".equalsIgnoreCase(key)){

                    String val = searchMap.get(key);
                    val = val.replace("+","%2B");

                    url.append(key + "=" + val);
                    flag++;
                }

            }
        }
        model.addAttribute("url",url.toString());
        return "search";
    }
}
