package com.changgou.business.listener;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AdUpdateRequestListener {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @RabbitListener(queues = "ad_update_queue")
    public void msgHandle(String position){
        //1.拼接广告更新的URL
        String url = "http://192.168.200.128/ad_update?position=" +position;
        //2.拼接请求对象
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();


        //3.执行请求
        Call call = okHttpClient.newCall(request);
        //4.处理响应结果
        call.enqueue(new Callback() {
            //请求失败的情况
            @Override
            public void onFailure(Call call, IOException e) {
                logger.error("大广告预热更新请求失败,position:{}" + position,e);
            }
            //请求成功的情况
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                logger.info("大广告预热更新成功,position:{}" + position);
            }
        });
    }
}
