package link.tomorinao.xuecheng.auth.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import link.tomorinao.xuecheng.auth.mapper.XcUserMapper;
import link.tomorinao.xuecheng.auth.mapper.XcUserRoleMapper;
import link.tomorinao.xuecheng.auth.model.dto.AuthParamsDto;
import link.tomorinao.xuecheng.auth.model.dto.XcUserExt;
import link.tomorinao.xuecheng.auth.model.po.XcUser;
import link.tomorinao.xuecheng.auth.model.po.XcUserRole;
import link.tomorinao.xuecheng.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service("wx_authservice")
public class WxAuthService implements AuthService {

    @Value("${weixin.appid}")
    String appid;

    @Value("${weixin.secret}")
    String secret;

    @Autowired
    XcUserMapper xcUserMapper;

    @Autowired
    XcUserRoleMapper xcUserRoleMapper;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    WxAuthService thisProxy;
    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        // 拿用户名查用户
        String username = authParamsDto.getUsername();
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>()
                .eq(XcUser::getUsername, username));
        if(xcUser==null){
            throw new RuntimeException("用户不存在");
        }
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtil.copyProperties(xcUser,xcUserExt);
        return xcUserExt;
    }

    public XcUser wxAuth(String code) {
        // 调微信接口，code换access_token
        Map<String, String> wxres = getAccessToken(code);
        String accessToken = wxres.get("access_token");
        String openid = wxres.get("openid");
        // 拿微信用户信息
        Map<String, String> userInfo = getUserInfo(accessToken, openid);
        // 用户信息存数据库
        XcUser xcUser = thisProxy.addWxUser(userInfo);
        return xcUser;
    }


    /*
    微信文档
    https://developers.weixin.qq.com/doc/oplatform/Website_App/WeChat_Login/Authorized_Interface_Calling_UnionID.html
    正确响应示例
    {
        "access_token":"ACCESS_TOKEN",
        "expires_in":7200,
        "refresh_token":"REFRESH_TOKEN",
        "openid":"OPENID",
        "scope":"SCOPE",
        "unionid": "o6_bmasdasdsad6_2sgVt7hMZOPfL"
    }
     */
    private Map<String, String> getAccessToken(String code) {
        // 1. 请求路径模板，参数用%s占位符
        String url_template = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
        // 2. 填充占位符：appid，secret，code
        String url = String.format(url_template, appid, secret, code);
        // 3. 远程调用URL，GET方式（详情参阅官方文档）
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        // 4. 获取相应结果，响应结果为json格式
        String result = exchange.getBody();
        // 5. 转为map
        Map<String, String> map = JSON.parseObject(result, Map.class);
        return map;
    }


    private Map<String, String> getUserInfo(String accessToken, String openid) {

        // 1. 请求路径模板，参数用%s占位符
        String url_template = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s";
        // 2. 填充占位符：appid，secret，code
        String url = String.format(url_template, accessToken, openid);
        // 3. 远程调用URL，GET方式（详情参阅官方文档）
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        // 4. 获取相应结果，响应结果为json格式
        String result = exchange.getBody();
        result = new String(result.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        // 5. 转为map
        Map<String, String> map = JSON.parseObject(result, Map.class);
        return map;
    }


    public XcUser addWxUser(Map<String, String> user_info_map) {
        // 1. 获取用户唯一标识：unionid作为用户的唯一表示
        String unionid = user_info_map.get("unionid");
        // 2. 根据唯一标识，判断数据库是否存在该用户
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getWxUnionid, unionid));
        // 2.1 存在，则直接返回
        if (xcUser != null) {
            return xcUser;
        }
        // 2.2 不存在，新增
        xcUser = new XcUser();
        // 2.3 设置主键
        String uuid = UUID.randomUUID().toString();
        xcUser.setId(uuid);
        // 2.4 设置其他数据库非空约束的属性
        xcUser.setUsername(unionid);
        xcUser.setPassword(unionid);
        xcUser.setWxUnionid(unionid);
        xcUser.setNickname(user_info_map.get("nickname"));
        xcUser.setUserpic(user_info_map.get("headimgurl"));
        xcUser.setName(user_info_map.get("nickname"));
        xcUser.setUtype("101001");  // 学生类型
        xcUser.setStatus("1");
        xcUser.setCreateTime(LocalDateTime.now());
        // 2.5 添加到数据库
        xcUserMapper.insert(xcUser);
        // 3. 添加用户信息到用户角色表
        XcUserRole xcUserRole = new XcUserRole();
        xcUserRole.setId(uuid);
        xcUserRole.setUserId(uuid);
        xcUserRole.setRoleId("17");
        xcUserRole.setCreateTime(LocalDateTime.now());
        xcUserRoleMapper.insert(xcUserRole);
        return xcUser;
    }
}