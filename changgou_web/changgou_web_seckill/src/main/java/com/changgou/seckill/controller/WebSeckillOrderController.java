package com.changgou.seckill.controller;

import com.changgou.entity.Constants;
import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.seckill.feign.SeckillOrderFeign;
import com.changgou.seckill.util.CookieUtil;
import com.changgou.util.RandomUtil;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.concurrent.TimeUnit;

import static org.bouncycastle.asn1.x500.style.RFC4519Style.l;
import static org.bouncycastle.asn1.x500.style.RFC4519Style.uid;

@Controller
@RequestMapping("/wseckillorder")
public class WebSeckillOrderController {


    @Autowired
    private SeckillOrderFeign seckillOrderFeign;

    @Autowired
    private RedisTemplate redisTemplate;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/getToken")
    @ResponseBody
    public String getToken(){

        String token = RandomUtil.getRandomString();
        String jti = getJti();
        //将随机生成的token,保存到当前登录用户对应的缓存结构中,过期时间是5秒
        redisTemplate.opsForValue().set(Constants.SECKILL_USER_KEY + jti, token,5, TimeUnit.SECONDS);
        return token;

    }

    private String getJti(){
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return CookieUtil.readCookie(servletRequestAttributes.getRequest(),"uid").get("id");
    }



    @GetMapping("/add")
    @ResponseBody
    public Result add(@RequestParam("time") String time, @RequestParam("id") Long id , @RequestParam("token")String token){

        String jti = getJti();
        String redisToken = (String) redisTemplate.opsForValue().get(Constants.SECKILL_USER_KEY + jti);
        //如果下单请求的参数token在缓存不存在,或者是跟缓存中的值不一样,都是非法请求应该屏蔽
        if (redisToken==null || !redisToken.equals(token)){
            logger.info("TOKEN非法,下单失败");
            return new Result(false,StatusCode.ERROR,"下单失败!");

        }

        Boolean addResult = seckillOrderFeign.add(time,id);

        if (addResult){
            return new Result(true, StatusCode.OK, "下单成功");
        }

        logger.info("下单失败");
        return new Result(false,StatusCode.ERROR, "下单失败");
    }
}
