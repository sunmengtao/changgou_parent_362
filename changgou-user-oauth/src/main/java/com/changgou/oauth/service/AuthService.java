package com.changgou.oauth.service;

import com.changgou.oauth.util.AuthToken;

public interface AuthService {

    /**
     * 密码模式申请令牌
     * @param clientId 第三方应用的ID
     * @param clientSecret 第三方应用的密码
     * @param username 消费者用户的用户名
     * @param password 消费者用户的密码
     * @return
     */
    AuthToken applyToken(String clientId,String clientSecret,String username,String password);
}
