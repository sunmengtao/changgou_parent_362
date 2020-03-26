package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.search.SkuInfo;
import com.changgou.search.service.EsSearchService;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EsSearchServiceImpl implements EsSearchService {


    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;


    @Override
    public Map search(Map<String, String> searchMap) {

        Map searchResult = new HashMap();//搜索结果的对象
        if(searchMap==null || searchMap.size()==0){
            return searchResult;
        }

        
        //构建组合查询搜索条件对象BooleanQueryBuilder
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //判断搜索条件是否为空
        if(StringUtils.isNotEmpty(searchMap.get("keywords"))){
            //需求1：根据输入框输入的商品关键词进行模糊搜索，类似于mysql中的select * from tb_sku where name like '%华为%'
            boolQueryBuilder.must(QueryBuilders.matchQuery("name",searchMap.get("keywords") ).operator(Operator.AND)); // must-and , should - or, mustnot - not
        }


        //构建顶级搜索条件对象
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //添加布尔查询对象
        nativeSearchQueryBuilder.withQuery(boolQueryBuilder);


        //需求2：根据品牌名称进行聚合
        //需求2.1：设置聚合分组名称
        String brandGroup = "brandGroup";
        TermsAggregationBuilder brandGroupBuilder = AggregationBuilders.terms(brandGroup).field("brandName");
        nativeSearchQueryBuilder.addAggregation(brandGroupBuilder);

        //执行搜索
        AggregatedPage<SkuInfo> search = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                long totalHits = searchResponse.getHits().getTotalHits(); //搜索命中的条数
                SearchHit[] hits = searchResponse.getHits().getHits();//搜索命中的记录
                List<T> skuList = new ArrayList<>();
                if(totalHits>0){
                    for (SearchHit hit : hits) {
                        String skuInfoJson = hit.getSourceAsString(); //搜索命中的每一条记录的JSON字符串
                        SkuInfo skuInfo = JSON.parseObject(skuInfoJson, SkuInfo.class);
                        skuList.add((T)skuInfo);
                    }
                }
                //第一个参数：搜索结果集合，第二个参数：分页结果对象 ， 第三发参数：搜索命中的条数，第四个参数：聚合结果对象
                return new AggregatedPageImpl<>(skuList, pageable, totalHits, searchResponse.getAggregations());
            }
        });

        //需求2.1：根据聚合分组名查找对应聚合结果集
        StringTerms brandTerms = (StringTerms)search.getAggregation(brandGroup);
        List<StringTerms.Bucket> brandBuckets = brandTerms.getBuckets();
        List<String> brandList = new ArrayList<>();//这里如果返回给前端，数据格式，将是['小米','华为','苹果']
        if(brandBuckets!=null && brandBuckets.size()>0){
            for (StringTerms.Bucket brandBucket : brandBuckets) {
                String brandValue = brandBucket.getKeyAsString();
                brandList.add(brandValue);
            }
        }


        searchResult.put("rows", search.getContent());//搜索结果记录，如果没有做分页配置那么此处默认是10条数据
        searchResult.put("total",search.getTotalElements());//搜索结果总条数
        searchResult.put("totalPage", search.getTotalPages()); //搜索结果总页数，如果没有做分页配置那么此处默认是1页
        searchResult.put("brandList", brandList);//品牌聚合结果集合，转换为JSON后格式如 ['小米','华为','苹果']

        return searchResult;
    }
}
