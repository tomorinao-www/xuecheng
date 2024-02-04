package link.tomorinao.xuecheng.checkcode.service.impl;

import cn.hutool.extra.mail.MailUtil;
import link.tomorinao.xuecheng.base.exception.XueChengException;
import link.tomorinao.xuecheng.checkcode.service.SendEmailService;
import link.tomorinao.xuecheng.checkcode.utils.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class SendEmailServiceImpl implements SendEmailService {
    private final long VCODE_TTL = 120L;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public void sendEmail(String email) {
        String vcode = EmailUtil.generateVerifyCode();
        String redisKey = email +"_findpwd";
        redisTemplate.opsForValue().set(redisKey, vcode, VCODE_TTL, TimeUnit.SECONDS);
        try {
            EmailUtil.sendMail(email, "找回密码邮件", "您用于找回密码的验证码为：" + vcode + "\n2分钟内有效");
        } catch (Exception e) {
            XueChengException.cast("发送邮件失败");
            redisTemplate.opsForValue().getAndDelete(redisKey);
        }
    }
}
