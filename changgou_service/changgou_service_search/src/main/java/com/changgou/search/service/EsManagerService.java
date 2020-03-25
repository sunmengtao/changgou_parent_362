package com.changgou.search.service;

public interface EsManagerService {

    /**
     * 删除ES的索引库和映射关系
     */
    void deleteIndexAndMapping();


    /**
     * 创建ES的索引库和映射关系
     */
    void createIndexAndMapping();

    /**
     * 根据spuId导入商品sku数据到ES中
     * @param spuId
     */
    void importBySpuId(String spuId);
}
