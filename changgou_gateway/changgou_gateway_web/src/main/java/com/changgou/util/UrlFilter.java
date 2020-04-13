package com.changgou.util;

public class UrlFilter {

    //购物车订单微服务都需要用户登录，必须携带令牌，所以所有路径都过滤,订单微服务需要过滤的地址
    public static String orderFilterPath = "/api/wpay,/api/wpay/**,/api/worder/**,/api/user/**,/api/address/**,/api/wcart/**,/api/cart/**,/api/categoryReport/**,/api/orderConfig/**,/api/order/**,/api/orderItem/**,/api/orderLog/**,/api/preferential/**,/api/returnCause/**,/api/returnOrder/**,/api/returnOrderItem/**";

    public static String seckillFilterPath = "/api/wseckillorder/**,/api/seckillorder/**";

    public static boolean hasAuthorize(String url){

        String[] strings = orderFilterPath.replace("**", "").split(",");
        for (String uri : strings) {

            if (url.startsWith(uri)){
                return true;
            }
        }
        String[] strings2 = seckillFilterPath.replace("**", "").split(",");
        for (String uri : strings2) {
            if (url.startsWith(uri)){
                return true;
            }
        }
        return false;
    }
}