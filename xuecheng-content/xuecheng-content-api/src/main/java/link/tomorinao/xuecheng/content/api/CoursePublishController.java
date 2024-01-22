package link.tomorinao.xuecheng.content.api;


import link.tomorinao.xuecheng.content.model.dto.CoursePreviewDto;
import link.tomorinao.xuecheng.content.model.po.CoursePublish;
import link.tomorinao.xuecheng.content.service.ICoursePublishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RestController
public class CoursePublishController {
    @Autowired
    ICoursePublishService iCoursePublishService;

    @GetMapping("/coursepreview/{courseId}")
    public ModelAndView preview(@PathVariable Long courseId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("course_template");
        CoursePreviewDto coursePreviewDto = iCoursePublishService.getCoursePreviewInfo(courseId);
        modelAndView.addObject("model", coursePreviewDto);
        return modelAndView;
    }

    @PostMapping("/coursepublish/{courseId}")
    @ResponseBody
    public void coursePublish(@PathVariable Long courseId) {
        iCoursePublishService.publishCourse(1232141425L, courseId);
    }

    @GetMapping("/r/coursepublish/{courseId}")
    @ResponseBody
    public CoursePublish getCoursePublish(@PathVariable Long courseId){
        return iCoursePublishService.getById(courseId);
    }
}