package link.tomorinao.xuecheng.content.service;

import link.tomorinao.xuecheng.content.model.dto.CourseInfoDto;

/**
 * <p>
 * 课程基本信息 服务类
 * </p>
 *
 * @author tomorinao
 * @since 2023-10-04
 */
public interface ICourseInfoService {

    Long add(Long companyId, CourseInfoDto dto);

    Long updateById(Long companyId, CourseInfoDto dto);

    CourseInfoDto getById(Long id);

    void deleteById(Long companyId, Long id);
}
