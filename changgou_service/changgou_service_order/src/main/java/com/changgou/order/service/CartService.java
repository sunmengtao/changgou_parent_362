package com.changgou.order.service;


import java.util.Map;

public interface CartService {

    void add(String username, String skuId, Integer num);

    Map list(String username);

    void updateChecked(String username,String skuId,Boolean checked);

    void delete(String username,String skuId);
}
