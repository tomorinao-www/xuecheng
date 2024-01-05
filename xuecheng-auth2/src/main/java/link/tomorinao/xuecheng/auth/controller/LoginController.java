package link.tomorinao.xuecheng.auth.controller;

import link.tomorinao.xuecheng.auth.mapper.XcUserMapper;
import link.tomorinao.xuecheng.auth.model.po.XcUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mr.M
 * @version 1.0
 * @description 测试controller
 * @date 2022/9/27 17:25
 */
@Slf4j
@RestController
public class LoginController {

    @Autowired
    XcUserMapper userMapper;


    @RequestMapping("/login-success")
    public String loginSuccess() {

        return "登录成功";
    }


    @RequestMapping("/user/{id}")
    public XcUser getuser(@PathVariable("id") String id) {
        XcUser xcUser = userMapper.selectById(id);
        return xcUser;
    }

    /*
    注解@PreAuthorize("hasAnyAuthority('p1')")加在方法上
    controller代理对象的这个方法就会被拦截，执行
    org.springframework.security.access.expression.SecurityExpressionRoot.hasAnyAuthority
    由自定义jwt生成器和解析器决定权限字段key，默认是scope，并且scope内容带前缀SCOPE_
     */
    @RequestMapping("/r/r1")
    @PreAuthorize("hasAnyAuthority('p1')")
    public String r1() {
        return "访问r1资源";
    }

    @RequestMapping("/r/r2")
    @PreAuthorize("hasAnyAuthority('p2')")
    public String r2() {
        return "访问r2资源";
    }



}
