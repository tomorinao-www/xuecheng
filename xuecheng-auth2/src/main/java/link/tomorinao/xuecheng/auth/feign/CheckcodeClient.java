package link.tomorinao.xuecheng.auth.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "checkcode")
public interface CheckcodeClient {
    @PostMapping(value = "/checkcode/verify")
    Boolean verify(@RequestParam("key") String key,
                          @RequestParam("code") String code);
}
