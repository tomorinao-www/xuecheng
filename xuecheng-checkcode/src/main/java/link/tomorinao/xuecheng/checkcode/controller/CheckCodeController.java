package link.tomorinao.xuecheng.checkcode.controller;

import link.tomorinao.xuecheng.checkcode.model.CheckCodeParamsDto;
import link.tomorinao.xuecheng.checkcode.model.CheckCodeResultDto;
import link.tomorinao.xuecheng.checkcode.service.CheckCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Mr.M
 * @version 1.0
 * @description 验证码服务接口
 * @date 2022/9/29 18:39
 */
@RestController
public class CheckCodeController {

    @Autowired
    private CheckCodeService picCheckCodeService;


    @PostMapping(value = "/pic")
    public CheckCodeResultDto generatePicCheckCode(CheckCodeParamsDto checkCodeParamsDto){
        return picCheckCodeService.generate(checkCodeParamsDto);
    }

    @PostMapping(value = "/verify")
    public Boolean verify(String key, String code){
        Boolean isSuccess = picCheckCodeService.verify(key,code);
        return isSuccess;
    }
}
