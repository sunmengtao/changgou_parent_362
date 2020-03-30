package com.changgou.oauth.service;

import com.changgou.oauth.util.AuthToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class AuthServiceImpl implements  AuthService {

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${auth.ttl}")
    private Long ttl;


    @Override
    public AuthToken applyToken(String clientId, String clientSecret, String username, String password) {
        //1.获取ecureka server中正在运行的user-oauth服务
        ServiceInstance userAuthInstance = loadBalancerClient.choose("user-auth");
        if(userAuthInstance==null){
            logger.error("user-auth服务不存在！");
            throw new RuntimeException("user-auth服务不存在！");
        }

        //2.根据user-oauth的IP和端口及申请令牌的URL拼接成即将要请求的完整的URL
        String url = userAuthInstance.getUri() + "/oauth/token";

        //3.设置请求头
        MultiValueMap<String,String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", getBasic(clientId,clientSecret));

        //4.设置请求体
        MultiValueMap<String,String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password"); //密码模式
        body.add( "username", username); //用户名
        body.add("password", password); //用户密码

        //5.构建请求对象
        HttpEntity<MultiValueMap<String,String>> httpEntity = new HttpEntity<>(body, headers);

        //6.执行密码模式申请令牌的请求
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

        //6.处理请求的响应，获取accessToken等信息分装为AuthToken，并将token和jti存入redis
        Map respMap = response.getBody();
        if(respMap==null || respMap.get("access_token")==null || respMap.get("refresh_token")==null || respMap.get("jti")==null){
            logger.info("密码模式申请令牌失败，clientId:{},username:{}",clientId, username);
            throw  new RuntimeException("密码模式申请令牌失败");
        }
        AuthToken authToken = new AuthToken();
        authToken.setAccessToken(String.valueOf(respMap.get("access_token")));
        authToken.setRefreshToken(String.valueOf(respMap.get("refresh_token")));
        authToken.setJti(String.valueOf(respMap.get("jti")));

        stringRedisTemplate.opsForValue().set(authToken.getJti(), authToken.getAccessToken(), ttl, TimeUnit.SECONDS);

        return authToken;
    }

    private String getBasic(String clientId, String clientSecret) {
        String basic = clientId + ":" + clientSecret;
        return "Basic " + Base64Utils.encodeToString(basic.getBytes(Charset.defaultCharset()));
    }
}
