package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.changgou.goods.feign.SkuFiegn;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.SkuInfo;
import com.changgou.search.dao.SearchMapper;
import com.changgou.search.service.EsManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EsManagerServiceImpl implements EsManagerService {

    @Autowired
    private SearchMapper searchMapper;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private SkuFiegn skuFiegn;

    @Override
    public void deleteIndexAndMapping() {
        elasticsearchTemplate.deleteIndex(SkuInfo.class);
    }

    @Override
    public void createIndexAndMapping() {
        elasticsearchTemplate.createIndex(SkuInfo.class);
        elasticsearchTemplate.putMapping(SkuInfo.class);
    }

    @Override
    public void importBySpuId(String spuId) {
        //根据spuId 查询sku列表
        List<Sku> skuList = skuFiegn.findBySpuId(spuId);
        if (skuList==null || skuList.size()==0){
            throw new RuntimeException("数据不存在");
        }
        String skuListJson = JSON.toJSONString(skuList);
        List<SkuInfo> skuInfoList = JSON.parseArray(skuListJson, SkuInfo.class);
        for (SkuInfo skuInfo : skuInfoList) {
            String specJson = skuInfo.getSpec();
            Map specMap = JSON.parseObject(specJson, Map.class);
            skuInfo.setSpecMap(specMap);
        }
        //将sku列表数据导入到ES中
        searchMapper.saveAll(skuInfoList);
    }
}
