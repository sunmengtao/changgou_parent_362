package com.changgou.service;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.feign.CategoryFeign;
import com.changgou.goods.feign.SkuFiegn;
import com.changgou.goods.feign.SpuFeign;
import com.changgou.goods.pojo.Category;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PageServiceHtml implements PageService {

    @Autowired
    private SpuFeign spuFeign;

    @Autowired
    private SkuFiegn skuFiegn;

    @Autowired
    private CategoryFeign categoryFeign;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${pagepath}")
    private String pagepath;

    @Override
    public Map buildPageData(String spuId) {
        Map pageData = new HashMap();

        //SPU数据
        Spu spu = spuFeign.findById(spuId);
        if(spu!=null){
            pageData.put("spu", spu);

            //三级分类数据
            Category category1 = categoryFeign.findById(spu.getCategory1Id());
            Category category2 = categoryFeign.findById(spu.getCategory2Id());
            Category category3 = categoryFeign.findById(spu.getCategory3Id());
            pageData.put("category1", category1);
            pageData.put("category2", category2);
            pageData.put("category3", category3);

            //SKU数据
            List<Sku> skuList = skuFiegn.findBySpuId(spuId);
            pageData.put("skuList", skuList);

            //图片列表数据
            List<Map> imageMapList = JSON.parseArray(spu.getImages(), Map.class);
            List<String> imageList = new ArrayList<>();
            if(imageMapList!=null&&imageMapList.size()>0){
                for (Map map : imageMapList) {
                    imageList.add(String.valueOf(map.get("url")));
                }
            }
            pageData.put("imageList", imageList);

            //规格MAP数据
            Map specificationList = JSON.parseObject(spu.getSpecItems(), Map.class);
            pageData.put("specificationList", specificationList);

        }


        return pageData;
    }

    @Override
    public void createPageHtml(String spuId) {
        Map pageData = buildPageData(spuId);

        Context context = new Context();
        context.setVariables(pageData);//给静态模板页设置所需全部数据

        File dir = new File(pagepath);
        if(!dir.exists()){
            dir.mkdirs();// 相当于linux的 mkdir -p  /data/aaa/bb/ccc
        }

        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File(pagepath + "\\" +spuId+ ".html"));
            //执行生成静态页面
            templateEngine.process("item", context, pw);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(pw!=null){
                pw.close();
            }
        }
    }
}
