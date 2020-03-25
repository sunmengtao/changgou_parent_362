package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.feign.SkuFiegn;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.SkuInfo;
import com.changgou.search.dao.SearchMapper;
import com.changgou.search.service.EsManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
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
        //elasticsearchTemplate.deleteIndex("skuinfo");//根据索引名称删除索引库和映射关系
        elasticsearchTemplate.deleteIndex(SkuInfo.class); //根据类删除索引库和映射关系，推荐使用
    }


    @Override
    public void createIndexAndMapping() {
        //elasticsearchTemplate.createIndex("skuinfo");//根据名称创建索引库
        elasticsearchTemplate.createIndex(SkuInfo.class);//根据类创建索引库，推荐使用
        elasticsearchTemplate.putMapping(SkuInfo.class);//根据类创建索引库映射关系
    }


    @Override
    public void importBySpuId(String spuId) {
        //根据spuId查询sku列表
        List<Sku> skuList = skuFiegn.findBySpuId(spuId);
        if(skuList==null || skuList.size()==0){
            throw new RuntimeException("数据不存在");
        }

        //将skuList转换为JSON字符串
        String skuListJson = JSON.toJSONString(skuList);
        //将JSON字符串转换为List<SkuInfo>
        List<SkuInfo> skuInfoList = JSON.parseArray(skuListJson, SkuInfo.class);
        for (SkuInfo skuInfo : skuInfoList) {
            String specJson = skuInfo.getSpec(); //规格的JSON字符串
            Map specMap = JSON.parseObject(specJson, Map.class); //将规格JSON字符串转化为MAP对象
            skuInfo.setSpecMap(specMap);
        }
        //将sku列表数据导入到ES中
        searchMapper.saveAll(skuInfoList);
    }


    @Override
    public void importAll() {
        //1.查询所有的sku列表数据
        List<Sku> skuList = skuFiegn.findAll();

        //2.sku类型转换为skuinfo类型
        String skuListJson = JSON.toJSONString(skuList);
        List<SkuInfo> skuInfoList = JSON.parseArray(skuListJson, SkuInfo.class);

        //3.处理specMap属性
        for (SkuInfo skuInfo : skuInfoList) {
           String specJson = skuInfo.getSpec();
            Map specMap = JSON.parseObject(specJson, Map.class);
            skuInfo.setSpecMap(specMap);
        }

        //4.保存所有数据到ES中
        searchMapper.saveAll(skuInfoList);

    }


    @Override
    public void deleteBySpuId(String spuId) {
        //根据spuId查找sku的列表数据
        List<Sku> skuList = skuFiegn.findBySpuId(spuId);
        if(skuList==null || skuList.size()==0){
            throw new RuntimeException("数据不存在");
        }
        for (Sku sku : skuList) {
            searchMapper.deleteById(Long.valueOf(sku.getId()));
        }
    }
}
