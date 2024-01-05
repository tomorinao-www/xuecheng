package link.tomorinao.xuecheng.auth.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import link.tomorinao.xuecheng.auth.feign.CheckcodeClient;
import link.tomorinao.xuecheng.auth.mapper.XcUserMapper;
import link.tomorinao.xuecheng.auth.model.dto.AuthParamsDto;
import link.tomorinao.xuecheng.auth.model.dto.XcUserExt;
import link.tomorinao.xuecheng.auth.model.po.XcUser;
import link.tomorinao.xuecheng.auth.service.AuthService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service("password_authservice")
public class PasswordAuthService implements AuthService {
    @Autowired
    XcUserMapper xcUserMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CheckcodeClient checkcodeClient;

    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        // 校验验证码
        String checkcode = authParamsDto.getCheckcode();
        String checkcodekey = authParamsDto.getCheckcodekey();
        if (StrUtil.isBlank(checkcode) || StrUtil.isBlank(checkcodekey)) {
            throw new RuntimeException("验证码为空");
        }
        Boolean verify = checkcodeClient.verify(checkcodekey, checkcode);
        if (!verify) {
            throw new RuntimeException("验证码输入错误");
        }
        // 1. 获取账号
        String username = authParamsDto.getUsername();
        // 2. 根据账号去数据库中查询是否存在
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>()
                .eq(XcUser::getUsername, username));
        // 3. 不存在抛异常
        if (xcUser == null) {
            throw new RuntimeException("账号不存在");
        }
        // 4. 校验密码
        // 4.1 获取用户输入的密码
        String passwordForm = authParamsDto.getPassword();
        // 4.2 获取数据库中存储的密码
        String passwordDb = xcUser.getPassword();
        // 4.3 比较密码
        boolean matches = passwordEncoder.matches(passwordForm, passwordDb);
        // 4.4 不匹配，抛异常
        if (!matches) {
            throw new RuntimeException("账号或密码错误");
        }
        // 4.5 匹配，封装返回
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcUser, xcUserExt);
        return xcUserExt;
    }
}
