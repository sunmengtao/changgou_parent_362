package com.github.wxpay.sdk;

import java.io.InputStream;

/**
 * 微信 支付 Java配置
 * 
 */
public class WeChatPayConfig extends WXPayConfig {
    String getAppID() {
        /**
         * appid是微信公众账号或开放平台APP的唯一标识，在公众平台申请公众账号或者在开放平台申请APP账号后，微信会自动分配对应的appid，
         * 用于标识该应用。
         *
         */
        return "wx8397f8696b538317";
    }

    String getMchID() {
        /**
         * 商户申请微信支付后，由微信支付分配的商户收款账号。
         */
        return "1473426802";
    }

    String getKey() {
        /**
         * 交易过程生成签名的密钥，仅保留在商户系统和微信支付后台，不会在网络中传播。
         * 商户妥善保管该Key，切勿在网络中传输，不能在其他客户端中存储，保证key不会被泄漏。
         */
        return "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb";
    }

    InputStream getCertStream() {
        return null;
    }

    IWXPayDomain getWXPayDomain() {
        return new IWXPayDomain() {
            public void report(String domain, long elapsedTimeMillis, Exception ex) {

            }
            public DomainInfo getDomain(WXPayConfig config) {
                return new DomainInfo("api.mch.weixin.qq.com",true);
            }
        };
    }
}