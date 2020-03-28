package com.itheima.service;

import java.util.Map;

public interface PageService {

    Map buildPageDate(String spuId);

    void createPageHtml(String spuId);
}
