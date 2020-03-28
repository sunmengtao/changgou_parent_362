package com.itheima.service;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.feign.CategoryFeign;
import com.changgou.goods.feign.SkuFiegn;
import com.changgou.goods.feign.SpuFiegn;
import com.changgou.goods.pojo.Category;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class PageServiceImpl implements PageService {

    @Autowired
    private SpuFiegn spuFiegn;

    @Autowired
    private SkuFiegn skuFiegn;

    @Autowired
    private CategoryFeign categoryFeign;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${pagepath}")
    private String pagepath;

    @Override
    public Map buildPageDate(String spuId) {
        Map pageData = new HashMap();
        Spu spu = spuFiegn.findById(spuId);
        if (spu!=null){
            pageData.put("spu",spu);

            Category category1 = categoryFeign.findById(spu.getCategory1Id());
            Category category2 = categoryFeign.findById(spu.getCategory2Id());
            Category category3 = categoryFeign.findById(spu.getCategory3Id());
            pageData.put("category1",category1);
            pageData.put("category2",category2);
            pageData.put("category3",category3);



            List<Sku> skuList = skuFiegn.findBySpuId(spuId);
            pageData.put("skuList",skuList);

            List<Map> imageMapList = JSON.parseArray(spu.getImages(), Map.class);
            List<String> imageList = new ArrayList<>();
            if (imageList!=null&&imageMapList.size()>0){
                for (Map map : imageMapList) {
                    imageList.add(String.valueOf(map.get("url")));
                }
            }
            pageData.put("imageList",imageList);

            Map specificationList = JSON.parseObject(spu.getSpecItems(), Map.class);
            pageData.put("specificationList",specificationList);
        }
        return pageData;
    }

    @Override
    public void createPageHtml(String spuId) {
        Map pageDate = buildPageDate(spuId);
        Context context = new Context();
        context.setVariables(pageDate);

        File dir = new File(pagepath);
        if (!dir.exists()){
            dir.mkdirs();
        }
        PrintWriter pw =null;
        try {
             pw = new PrintWriter(new File(pagepath+"/"+".html"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pw!=null){
                pw.close();
            }
        }


        templateEngine.process("item",context);
    }
}
