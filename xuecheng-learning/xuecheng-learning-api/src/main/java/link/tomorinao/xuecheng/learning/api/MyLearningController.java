package link.tomorinao.xuecheng.learning.api;

import link.tomorinao.xuecheng.base.model.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mr.M
 * @version 1.0
 * @description 我的学习接口
 * @date 2022/10/27 8:59
 */
@Slf4j
@RestController
public class MyLearningController {


    @GetMapping("/open/learn/getvideo/{courseId}/{teachplanId}/{mediaId}")
    public RestResponse<String> getvideo(@PathVariable("courseId") Long courseId,
                                         @PathVariable("courseId") Long teachplanId,
                                         @PathVariable("mediaId") String mediaId) {

        return null;

    }

}
