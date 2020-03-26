package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.search.SkuInfo;
import com.changgou.search.service.EsSearchService;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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


        //需求5：根据品牌名称进行精确搜索，类似于mysql中的select * from tb_sku where brand_name='华为'
        if(StringUtils.isNotEmpty(searchMap.get("brand"))){
            boolQueryBuilder.filter(QueryBuilders.termQuery("brandName", searchMap.get("brand")));
        }

        //需求6：根据分类名称进行精确搜索，类似于mysql的select * from tb_sku where category_name = '老花镜'
        if(StringUtils.isNotEmpty(searchMap.get("categoryName"))){
            boolQueryBuilder.filter(QueryBuilders.termQuery("categoryName", searchMap.get("categoryName")));
        }

        //需求7：根据规格进行精确搜索，类似于mysql的select * from tb_sku where spec_color='红色'
        for(String key : searchMap.keySet()){
            if(key.startsWith("spec_")){ //key的格式类似于：spec_内存、spc_颜色
                String[] s = key.split("_");
                if(s.length==2){
                    String specName = s[1]; // 这里取出的结果应该是 内存、颜色 这样的规格名
                    String fieldName = "specMap." + specName + ".keyword";
                    String specVal = searchMap.get(key);
                    boolQueryBuilder.filter(QueryBuilders.termQuery(fieldName, specVal));
                }
            }
        }

        //需求8：根据价格区间进行范围搜索，类似于myssql的select * from tb_sku where price between 1000 and 3000
        if(StringUtils.isNotEmpty(searchMap.get("price"))){
            String price = searchMap.get("price"); //价格区间的值格式如： 1000-3000
            String[] split = price.split("-");
            if(split.length==2){
                String lowPrice = split[0];//获取价格区间最小价格
                String highPrice = split[1];//获取价格区间最大价格
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(lowPrice).lte(highPrice));
            }
        }



        //构建顶级搜索条件对象
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //添加布尔查询对象
        nativeSearchQueryBuilder.withQuery(boolQueryBuilder);


        //需求2：根据品牌名称进行聚合，类似于mysql的select brand_name from tb_sku group brand_name
        //需求2.1：设置聚合分组名称
        String brandGroup = "brandGroup";
        TermsAggregationBuilder brandGroupBuilder = AggregationBuilders.terms(brandGroup).field("brandName");
        nativeSearchQueryBuilder.addAggregation(brandGroupBuilder);


        //需求3：根据分类名称进行聚合，类似于mysql的select category_name from tb_sku group by category_name
        //需求3.1：设置聚合分组名称
        String cateGroup = "cateGroup";
        TermsAggregationBuilder cateGroupBuilder = AggregationBuilders.terms(cateGroup).field("categoryName");
        nativeSearchQueryBuilder.addAggregation(cateGroupBuilder);


        //需求4：根据规格进行聚合，类似于mysql的select spec from tb_sku group by spec
        //需求4.1:设置聚合分组名称
        String specGroup = "specGroup";
        TermsAggregationBuilder specGroupBuilder = AggregationBuilders.terms(specGroup).field("spec.keyword");
        nativeSearchQueryBuilder.addAggregation(specGroupBuilder);


        //需求9：根据价格等进行排序，类似于mysql的select * from tb_sku order by price desc
        if(StringUtils.isNotEmpty(searchMap.get("sortField")) && StringUtils.isNotEmpty(searchMap.get("sortRule"))){
            String sortField = searchMap.get("sortField");
            String sortRule = searchMap.get("sortRule");
            if("DESC".equalsIgnoreCase(sortRule)){
                nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(sortField).order(SortOrder.DESC));
            } else {
                nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(sortField).order(SortOrder.ASC));
            }
        }

        //需求10：分页设置，类似于mysql的select * from tb_sku limit 10,20
        int pageNum = 1;
        int pageSize = 20;
        if(StringUtils.isNotEmpty(searchMap.get("pageNum"))){
            pageNum = Integer.valueOf(searchMap.get("pageNum"));
        }

        if(StringUtils.isNotEmpty(searchMap.get("pageSize"))){
            pageSize = Integer.valueOf(searchMap.get("pageSize"));
        }
        //ES中分页查询pageNo第一页是0，所以要减1
        nativeSearchQueryBuilder.withPageable(PageRequest.of(pageNum-1, pageSize));


        //需求11：高亮设置
        //需求11.1：设置高亮HTML标签
        HighlightBuilder.Field hightlightField = new HighlightBuilder.Field("name").preTags("<span style='color:red'>").postTags("</span>");
        nativeSearchQueryBuilder.withHighlightFields(hightlightField);


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
                        //需求11.2：取出高亮名称，设置到sku对象中
                        HighlightField field = hit.getHighlightFields().get("name");

                        //有高亮字段的时候才处理高亮的名称
                        if(field!=null){
                            Text[] fragments = field.getFragments();
                            if(fragments!=null){
                                String highlightName = fragments[0].toString();
                                skuInfo.setName(highlightName);
                            }
                        }

                        skuList.add((T)skuInfo);
                    }
                }
                //第一个参数：搜索结果集合，第二个参数：分页结果对象 ， 第三发参数：搜索命中的条数，第四个参数：聚合结果对象
                return new AggregatedPageImpl<>(skuList, pageable, totalHits, searchResponse.getAggregations());
            }
        });

        //需求2.2 根据聚合分组名查找对应聚合结果集（品牌名称的去重结果集）
        StringTerms brandTerms = (StringTerms)search.getAggregation(brandGroup);
        List<StringTerms.Bucket> brandBuckets = brandTerms.getBuckets();
        List<String> brandList = new ArrayList<>();//这里如果返回给前端，数据格式，将是['小米','华为','苹果']
        if(brandBuckets!=null && brandBuckets.size()>0){
//            for (StringTerms.Bucket brandBucket : brandBuckets) {
//                String brandValue = brandBucket.getKeyAsString();
//                brandList.add(brandValue);
//            }
            brandList = brandBuckets.stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
        }

        //需求3.2 根据聚合分组名查找对应聚合结果集（分类名称的去重结果集）
        StringTerms cateTerms = (StringTerms)search.getAggregation(cateGroup);
        List<StringTerms.Bucket> cateBuckets = cateTerms.getBuckets();
        List<String> cateList = new ArrayList<>();
        if(cateBuckets!=null && cateBuckets.size()>0){
            cateList = cateBuckets.stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
        }

        
        //需求4.2 根据规格分组名查找对应聚合结果集（规格JSON的去重结果集）
        /**
         * 这里的规格JSON字符串有可能是这样：
         * {'颜色': '蓝色'}
         * {'颜色': '蓝色', '版本': '4GB+64GB'}
         * {'颜色': '红色', '版本': '4GB+64GB'}
         * {'颜色': '红色', '版本': '4GB+32GB'}
         *
         * 这种结果需要再一次去重，得到前端更方便解析的格式：
         * {
         *     '颜色':['蓝色','红色'],
         *     '版本':['4GB+32GB', '4GB+64GB']
         * }
         */
        Map<String, Set<String>> specMap = new HashMap<>();
        StringTerms specTerms = (StringTerms)search.getAggregation(specGroup);
        List<StringTerms.Bucket> specBuckets = specTerms.getBuckets();
        if(specBuckets!=null && specBuckets.size()>0){
            for (StringTerms.Bucket specBucket : specBuckets) {
                String specJson = specBucket.getKeyAsString();//去重之后的规格JSON字符串
                Map<String,String> map = JSON.parseObject(specJson, Map.class);
                if(map!=null && map.size()>0){
                    Set<String> specValSet = null;//用于存放规格值的SET，可以去重
                    for(String key : map.keySet()){
                        if(!specMap.containsKey(key)){
                            specValSet = new HashSet<>();
                        } else {
                            specValSet = specMap.get(key);
                        }
                        String specVal = map.get(key);
                        specValSet.add(specVal);
                        specMap.put(key, specValSet);
                    }
                }

            }
        }

        searchResult.put("rows", search.getContent());//搜索结果记录，如果没有做分页配置那么此处默认是10条数据
        searchResult.put("total",search.getTotalElements());//搜索结果总条数
        searchResult.put("totalPage", search.getTotalPages()); //搜索结果总页数，如果没有做分页配置那么此处默认是1页
        searchResult.put("brandList", brandList);//品牌聚合结果集合，转换为JSON后格式如 ['小米','华为','苹果']
        searchResult.put("cateList", cateList); //分类名称聚合结果集合，转换为JSON格式如['手机','电脑','办公桌']
        searchResult.put("specList", specMap); //规格聚合结果集合，转换为JSON格式如{ '颜色':['蓝色','红色'], '版本':['4GB+32GB', '4GB+64GB']}
        return searchResult;
    }
}
