package com.changgou.goods.dao;

import com.changgou.goods.pojo.Sku;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

public interface SkuMapper extends Mapper<Sku> {

    @Update("update tb_sku set num=num-#{num},sale_num=sale_num+#{num} where id=#{skuId} and num>=#{num}")
    int decrCount(@Param("skuId") String skuId, @Param("num") Integer num);


    @Update("update tb_sku set num=num+#{num},sale_num=sale_num-#{num} where id=#{skuId} and sale_num>=#{num}")
    int incrCount(@Param("skuId") String skuId, @Param("num") Integer num);
}
