package link.tomorinao.xuecheng.search.controller;

import link.tomorinao.xuecheng.base.exception.XueChengException;
import link.tomorinao.xuecheng.search.po.CourseIndex;
import link.tomorinao.xuecheng.search.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mr.M
 * @version 1.0
 * @description 课程索引接口
 * @date 2022/9/24 22:31
 */
@RestController
@RequestMapping("/index")
public class CourseIndexController {

    @Value("${elasticsearch.course.index}")
    private String courseIndexStore;

    @Autowired
    IndexService indexService;

    @PostMapping("/course")
    public Boolean add(@RequestBody CourseIndex courseIndex) {

        Long id = courseIndex.getId();
        if(id==null){
            XueChengException.cast("课程id为空");
        }
        Boolean result = indexService.putDoc(courseIndexStore, String.valueOf(id), courseIndex);
        if(!result){
            XueChengException.cast("添加课程索引失败");
        }
        return result;

    }
}
