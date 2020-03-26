package com.changgou.search.service;

import java.util.Map;

public interface EsSearchService {

    /**
     * 根据搜索条件进行搜索
     * @param searchMap 搜索条件
     * @return
     */
    Map search(Map<String,String> searchMap);
}
