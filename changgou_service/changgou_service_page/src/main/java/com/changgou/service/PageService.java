package com.changgou.service;

import java.util.Map;

public interface PageService {

    /**
     * 构建模板页面所需数据
     * @param spuId
     * @return
     */
    Map buildPageData(String spuId);

    /**
     * 利用模板引擎生成商品详情静态页面
     * @param spuId
     */
    void createPageHtml(String spuId);


    /**
     * 根据SPUID删除商品详情静态页面
     * @param spuId
     */
    void deletePageHtml(String spuId);
}
