package link.tomorinao.xuecheng.content.api;

import link.tomorinao.xuecheng.content.model.dto.CoursePreviewDto;
import link.tomorinao.xuecheng.content.service.ICoursePublishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/open")
public class CourseOpenController {
    @Autowired
    private ICoursePublishService iCoursePublishService;

    @GetMapping("/course/whole/{courseId}")
    public CoursePreviewDto getPreviewInfo(@PathVariable Long courseId){
        // 获取课程预览信息
        return iCoursePublishService.getCoursePreviewInfo(courseId);
    }
}