package com.changgou.search.service;


public interface EsManagerService {

    //删除ES的索引库的映射关系
    void deleteIndexAndMapping();

    //创建ES的索引库和映射关系
    void createIndexAndMapping();

    void importBySpuId(String spuId);
}
