package link.tomorinao.xuecheng.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import link.tomorinao.xuecheng.content.model.dto.CoursePreviewDto;
import link.tomorinao.xuecheng.content.model.po.CoursePublish;

import java.io.File;

/**
 * <p>
 * 课程发布 服务类
 * </p>
 *
 * @author tomorinao
 * @since 2023-10-04
 */
public interface ICoursePublishService extends IService<CoursePublish> {
    CoursePreviewDto getCoursePreviewInfo(Long courseId);

    void publishCourse(Long companyId, Long courseId);

    File generateCourseHtml(Long courseId) throws Exception;

    void uploadCourseHtml(Long courseId, File file);
}
