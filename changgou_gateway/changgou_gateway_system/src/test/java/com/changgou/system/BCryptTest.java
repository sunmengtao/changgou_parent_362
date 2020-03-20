package com.changgou.system;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class BCryptTest {

    //$2a$10$CozzIsraxifQ0Tea.1Xei.R22F56U1oSDaG1aiFE6mjPX9imGdOt6
    @Test
    public void testEncrypt(){
        String pwdText = "itheima";
        String pwdEncrypt = BCrypt.hashpw(pwdText, BCrypt.gensalt());
        System.out.println("加密后的密码:" + pwdEncrypt);
    }

    @Test
    public void testCheck(){
        String pwdText = "itheima";
        boolean result = BCrypt.checkpw(pwdText, "$2a$10$CozzIsraxifQ0Tea.1Xei.R22F56U1oSDaG1aiFE6mjPX9imGdOt6");
        System.out.println("密码校验结果:" + result);
    }
}
