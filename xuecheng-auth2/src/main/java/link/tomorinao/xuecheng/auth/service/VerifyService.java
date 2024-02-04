package link.tomorinao.xuecheng.auth.service;

import link.tomorinao.xuecheng.auth.model.dto.FindPwdDto;
import link.tomorinao.xuecheng.auth.model.dto.RegisterDto;

public interface VerifyService {
    void findPassword(FindPwdDto findPswDto);
    void register(RegisterDto registerDto);
}