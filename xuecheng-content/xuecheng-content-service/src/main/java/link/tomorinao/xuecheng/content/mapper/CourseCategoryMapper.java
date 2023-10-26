package link.tomorinao.xuecheng.content.mapper;

import link.tomorinao.xuecheng.content.model.dto.CourseCategoryTreeDto;
import link.tomorinao.xuecheng.content.model.po.CourseCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 课程分类 Mapper 接口
 * </p>
 *
 * @author tomorinao
 * @since 2023-10-04
 */
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {
    List<CourseCategoryTreeDto> selectTreeNodes(String id);
}
