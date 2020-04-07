package com.changgou.pay.service;

import java.util.Map;

public interface PayService {

    Map nativePay(String username);

    Map queryOrder(String orderId);

    void closeOrder(String orderId);
}
