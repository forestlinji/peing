/**
 * Copyright (c) 2016-2019 谷粒开源 All rights reserved.
 *
 * https://www.guli.cloud
 *
 * 版权所有，侵权必究！
 */

package peing.service.impl;



import com.google.code.kaptcha.Producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import peing.service.SysCaptchaService;

import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

/**
 * 验证码
 *
 * @author Mark sunlightcs@gmail.com
 */
@Service
public class SysCaptchaServiceImpl  implements SysCaptchaService {
    @Autowired
    private Producer producer;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public BufferedImage getCaptcha(String uuid) {
        //生成文字验证码
        String code = producer.createText();
        System.out.println(redisTemplate);
        redisTemplate.opsForValue().set("captcha:"+uuid,code);
        redisTemplate.expire("captcha:"+uuid,10, TimeUnit.MINUTES);
        return producer.createImage(code);
    }

    @Override
    public boolean validate(String uuid, String code) {
        Object ans = redisTemplate.opsForValue().get("captcha:" + uuid);
        if(ans==null){
            return false;
        }
        redisTemplate.delete("captcha:"+uuid);
        return code.equals((String)ans);
    }
}
