package com.changgou.token;

import com.changgou.OAuthApplication;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

@SpringBootTest(classes = OAuthApplication.class)
@RunWith(SpringRunner.class)
public class ApplyTokenTest {

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 测试申请令牌
     */
    @Test
    public void testApplyToken(){
        //获取ecureka server中正在运行的user-oauth服务
        ServiceInstance userAuthInstance = loadBalancerClient.choose("user-auth");
        Assert.assertNotNull(userAuthInstance);

        String clientId = "jd"; //第三方应用的ID
        String clientSecret = "123456"; //第三方应用的密码
        String username = "student"; //消费者用户的用户名
        String password = "123456"; //消费者用户的用户密码


        //根据user-oauth的IP和端口及申请令牌的URL拼接成即将要请求的完整的URL
        String url = userAuthInstance.getUri() + "/oauth/token";

        //设置请求头
        MultiValueMap<String,String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", getBasic(clientId,clientSecret));

        //设置请求体
        MultiValueMap<String,String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password"); //密码模式
        body.add( "username", username); //用户名
        body.add("password", password); //用户密码

        //构建请求对象
        HttpEntity<MultiValueMap<String,String>> httpEntity = new HttpEntity<>(body, headers);

        //执行密码模式申请令牌的请求
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            //400和401响应码不抛出异常
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if(response.getRawStatusCode()!=400 && response.getRawStatusCode()!=401){
                    super.handleError(response);
                }
            }
        });

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Map.class);

        //处理请求的响应，获取accessToken等
        Map respMap = response.getBody();
        if(respMap!=null){
            System.out.println("access_token:" + respMap.get("access_token"));
            System.out.println("refresh_token:" + respMap.get("refresh_token"));
            System.out.println("jti:" + respMap.get("jti"));
        }

    }

    private String getBasic(String clientId, String clientSecret) {
        String basic = clientId + ":" + clientSecret;
        return "Basic " + Base64Utils.encodeToString(basic.getBytes(Charset.defaultCharset()));
    }
}
