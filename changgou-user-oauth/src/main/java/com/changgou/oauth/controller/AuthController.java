package com.changgou.oauth.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.oauth.service.AuthService;
import com.changgou.oauth.util.AuthToken;
import com.changgou.oauth.util.CookieUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Controller
@RequestMapping("/oauth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Value("${auth.clientId}")
    private String clientId;

    @Value("${auth.clientSecret}")
    private String clientSecret;

    @Value("${auth.cookieDomain}")
    private String cookieDomain;

    @Value("${auth.cookieMaxAge}")
    private Integer cookieMaxAge;

    @GetMapping("/toLogin")
    public String toLogin(@RequestParam(name = "ReturnUrl",required = false,defaultValue = "http://www.changgou.com") String ReturnUrl, Model model){
        model.addAttribute("ReturnUrl", ReturnUrl);
        return "login";
    }




    /**
     * 测试登录的接口
     */
    @PostMapping("/interface/login")
    @ResponseBody
    public Result interfaceLogin(@RequestParam("username") String username,@RequestParam("password") String password){

        if(StringUtils.isEmpty(username)){
            throw new RuntimeException("用户名不能为空");
        }

        if(StringUtils.isEmpty(password)){
            throw new RuntimeException("密码不能为空");
        }

        try {
            AuthToken authToken = authService.applyToken(clientId, clientSecret, username, password);
            String jti = authToken.getJti();
            saveJtiToCookie(jti);

            return new Result(true, StatusCode.OK,  "登录成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, StatusCode.LOGINERROR,  "登录失败");
        }

    }

    /**
     * 保存JTI到浏览器cookie
     * @param jti
     */
    private void saveJtiToCookie(String jti) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        CookieUtil.addCookie(requestAttributes.getResponse(), cookieDomain, "/", "uid", jti, cookieMaxAge, true);
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username,@RequestParam("password") String password,@RequestParam(name = "ReturnUrl",required = false,defaultValue = "http://www.changgou.com") String ReturnUrl){

        if(StringUtils.isEmpty(username)){
            throw new RuntimeException("用户名不能为空");
        }

        if(StringUtils.isEmpty(password)){
            throw new RuntimeException("密码不能为空");
        }

        try {
            AuthToken authToken = authService.applyToken(clientId, clientSecret, username, password);
            String jti = authToken.getJti();
            saveJtiToCookie(jti);
            //登录成功之后,重定向跳转到某一页面
            return "redirect:" + ReturnUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:http://web.changgou.com:8001/api/oauth/toLogin?ReturnUrl="+ReturnUrl;
        }

    }
}
