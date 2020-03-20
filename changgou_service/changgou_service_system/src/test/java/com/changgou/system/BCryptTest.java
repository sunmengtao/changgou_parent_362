package com.changgou.system;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * BCRYPT加密与校验测试
 */
public class BCryptTest {


    /**
     * BCrypt加密测试
     * 测试场景：管理后台新增用户账号和密码，密码保存到DB之前应该先进行加密。
     *
     * 加密后的密码：$2a$10$j4ZuoF.7WaTiaobrqZ7gDuNag7atZN8ffpJpcsa/XOfs9wtvkipPK
     * 加密后的密码：$2a$10$dzvVpYsAnNfdNcYgh5J5Lec0r0QBf9F52UGK30.w9ooUw5UQfOn0m
     * 加密后的密码：$2a$10$VzJ3PYhx8I.vp.VRodO/suZ8bh.VTalYVKKOdTnsPKqq2rb1XILJa
     * 加密后的密码：$2a$10$0L5aux0S1QACJpSVdjQQ/eDuLa30PxBDzqu2h767QUBNDyHHGN6zu
     * 加密后的密码：$2a$10$2tlFyOZ7q/s7NhiKYohIgexyvIqUyENwOSMkYj4lPhsILDJnWl0Ky
     * 加密后的密码：$2a$10$ZWniPIDNbJUzJpI.Qf9Jiu0TsO1ITGVS201WQPNV8JeEyKyW5rXYO
     * 加密后的密码：$2a$10$wcttclbGoS3ZYEIcbvHG2OygZFoczR7M/UEtw7A6KG3BG1jmM5vvS
     * 加密后的密码：$2a$10$hIz3LQjyhjPQypQn.53zzuyruM.4KNVkGXmQIhsfu4VIH1NCcWtXi
     * 加密后的密码：$2a$10$JIi6EmmY1GwVz3TgyDf7Ie1EpXJP0SKa96qa3diBvsV8R1tC9hRQC
     * 加密后的密码：$2a$10$8F1c2gJrn.n0e3ROAWsgmuSzMCAGnnB5TG6z.llDgdcxvcDbi8Gmu
     */
    @Test
    public void testEncrypt(){
        for(int i=0; i<10; i++){
            //用户的明文密码
            String pwdText = "itheima";
            //对用户的明文密码进行加密
            String pwdEncrypt = BCrypt.hashpw(pwdText, BCrypt.gensalt());
            System.out.println("加密后的密码：" + pwdEncrypt);
        }
    }


    /**
     * 校验密码测试
     * 测试场景：管理后台用户登录时，校验用户输入的明文密码是否与该用户对应数据库表中的密文密码是一致
     */
    @Test
    public void testCheck(){
        //用户的登录密码
        String pwdText = "itheima";
        boolean result = BCrypt.checkpw(pwdText, "$2a$10$8F1c2gJrn.n0e3ROAWsgmuSzMCAGnnB5TG6z.llDgdcxvcDbi8Gmu");
        System.out.println("密码校验结果：" + result);
    }
}
