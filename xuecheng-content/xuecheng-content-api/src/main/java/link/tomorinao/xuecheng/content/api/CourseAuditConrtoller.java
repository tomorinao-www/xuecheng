package link.tomorinao.xuecheng.content.api;


import link.tomorinao.xuecheng.content.service.ICourseAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/courseaudit")
public class CourseAuditConrtoller {

    @Autowired
    ICourseAuditService iCourseAuditService;

    @PostMapping("/commit/{courseId}")
    public void commitAudit(@PathVariable Long courseId) {
        iCourseAuditService.commitAudit(1232141425L, courseId);
    }
}
