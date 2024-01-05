package link.tomorinao.xuecheng.auth.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import link.tomorinao.xuecheng.auth.mapper.XcUserMapper;
import link.tomorinao.xuecheng.auth.model.dto.AuthParamsDto;
import link.tomorinao.xuecheng.auth.model.dto.XcUserExt;
import link.tomorinao.xuecheng.auth.model.po.XcUser;
import link.tomorinao.xuecheng.auth.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private XcUserMapper xcUserMapper;
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * @param name 用户输入的登录账号
     * @return UserDetails
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        // 自定json解析
        AuthParamsDto authParamsDto;
        try {
            authParamsDto = JSON.parseObject(name, AuthParamsDto.class);
        } catch (Exception e) {
            log.error("认证请求数据格式不对：{}", name);
            throw new RuntimeException("认证请求数据格式不对");
        }
        // 获取认证类型，beanName就是 认证类型 + 后缀，例如 password + _authservice = password_authservice
        String authType = authParamsDto.getAuthType();
        // 根据认证类型，从Spring容器中取出对应的bean
        AuthService authService = applicationContext.getBean(authType + "_authservice", AuthService.class);
        XcUserExt user = authService.execute(authParamsDto);

        // 返回NULL表示用户不存在，SpringSecurity会帮我们处理，框架抛出异常用户不存在
        if (user == null) {
            return null;
        }
        // 取出数据库存储的密码
        String password = user.getPassword();
        // 敏感信息不放
//        user.setPassword(null);
        String userJSON = JSON.toJSONString(user);
        //如果查到了用户拿到正确的密码，最终封装成一个UserDetails对象给spring security框架返回，由框架进行密码比对
        return User.withUsername(userJSON).password(password).authorities("test").build();
    }
}
