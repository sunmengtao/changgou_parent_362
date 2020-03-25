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


    /**
     * 将sku表中所有的数据导入到ES中
     */
    void importAll();


    /**
     * 根据SPUID从ES中删除SKU列表数据
     * @param spuId
     */
    void deleteBySpuId(String spuId);
}
