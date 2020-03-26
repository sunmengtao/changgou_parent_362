package com.changgou.search.controller;

import com.changgou.search.service.EsSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/search")
public class EsSearchController {

    @Autowired
    private EsSearchService esSearchService;

    @GetMapping("/list")
    public Map search(@RequestParam Map<String, String> searchMap){
        Map result = esSearchService.search(searchMap);
        return result;
    }
}
