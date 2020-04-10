package com.changgou.seckill.service;

import com.changgou.seckill.pojo.SeckillGoods;

import java.util.List;

public interface SeckillGoodsService {

    void authoPush();

    List<SeckillGoods> list(String time);
}
