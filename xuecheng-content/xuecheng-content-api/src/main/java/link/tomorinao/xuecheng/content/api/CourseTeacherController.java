package link.tomorinao.xuecheng.content.api;

import jakarta.annotation.Resource;
import link.tomorinao.xuecheng.content.model.po.CourseTeacher;
import link.tomorinao.xuecheng.content.service.ICourseTeacherService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courseTeacher")
public class CourseTeacherController {

    @Resource
    private ICourseTeacherService iCourseTeacherService;

    @GetMapping("/list/{courseId}")
    public List<CourseTeacher> getBycourseId(@PathVariable Long courseId) {
        return iCourseTeacherService.getBycourseId(courseId);
    }

    @PostMapping
    public CourseTeacher addOrUpdate(@RequestBody CourseTeacher po) {
        iCourseTeacherService.saveOrUpdate(po);
        return iCourseTeacherService.getById(po.getId());
    }

    @DeleteMapping("/course/{courseId}/{id}")
    public void deleteById(@PathVariable Long id){
        iCourseTeacherService.removeById(id);
    }
}
