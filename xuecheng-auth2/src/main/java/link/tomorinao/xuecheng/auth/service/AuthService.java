package link.tomorinao.xuecheng.auth.service;

import link.tomorinao.xuecheng.auth.model.dto.AuthParamsDto;
import link.tomorinao.xuecheng.auth.model.dto.XcUserExt;

/**
 * 认证Service
 */
public interface AuthService {
    /**
     * 认证方法
     * @param authParamsDto 认证参数
     * @return  用户信息
     */
    XcUserExt execute(AuthParamsDto authParamsDto);
}