package link.tomorinao.xuecheng.content.service;

import link.tomorinao.xuecheng.base.model.PageParams;
import link.tomorinao.xuecheng.base.model.PageResult;
import link.tomorinao.xuecheng.content.model.dto.QueryCourseParamsDto;
import link.tomorinao.xuecheng.content.model.po.CourseBase;

/**
 * <p>
 * 课程基本信息 服务类
 * </p>
 *
 * @author tomorinao
 * @since 2023-10-04
 */
public interface ICourseBaseService{

    PageResult<CourseBase> list(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);

//    CourseBaseInfoDto add(Long companyId, CourseInfoDto dto);
//
//    CourseBaseInfoDto getById(Long id);
}
