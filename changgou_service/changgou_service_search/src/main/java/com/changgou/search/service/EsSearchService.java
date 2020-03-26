package com.changgou.search.service;

import java.util.Map;

public interface EsSearchService {

    //根据搜索条件进行搜索
    Map search(Map<String,String> searchMap);
}
