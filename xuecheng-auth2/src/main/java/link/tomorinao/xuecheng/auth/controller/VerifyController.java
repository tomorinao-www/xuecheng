package link.tomorinao.xuecheng.auth.controller;

import link.tomorinao.xuecheng.auth.model.dto.FindPwdDto;
import link.tomorinao.xuecheng.auth.model.dto.RegisterDto;
import link.tomorinao.xuecheng.auth.service.VerifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VerifyController {
    @Autowired
    VerifyService verifyService;

    @PostMapping("/findpassword")
    public void findPassword(@RequestBody FindPwdDto findPwdDto) {
        verifyService.findPassword(findPwdDto);
    }

    @PostMapping("/register")
    public void register(@RequestBody RegisterDto registerDto) {
        verifyService.register(registerDto);
    }
}
