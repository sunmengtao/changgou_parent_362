package com.changgou.search.dao;

import com.changgou.search.SkuInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SearchMapper extends ElasticsearchRepository<SkuInfo, Long> {
}
