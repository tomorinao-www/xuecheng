package link.tomorinao.xuecheng.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import link.tomorinao.xuecheng.content.model.dto.CourseCategoryTreeDto;
import link.tomorinao.xuecheng.content.model.po.CourseCategory;

import java.util.List;

/**
 * <p>
 * 课程分类 服务类
 * </p>
 *
 * @author tomorinao
 * @since 2023-10-04
 */
public interface ICourseCategoryService extends IService<CourseCategory> {

    List<CourseCategoryTreeDto> queryTreeNodes(String id);
}
