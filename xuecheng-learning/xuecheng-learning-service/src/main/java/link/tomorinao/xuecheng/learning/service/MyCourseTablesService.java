package link.tomorinao.xuecheng.learning.service;

import link.tomorinao.xuecheng.learning.model.dto.XcChooseCourseDto;
import link.tomorinao.xuecheng.learning.model.dto.XcCourseTablesDto;

public interface MyCourseTablesService {
    XcChooseCourseDto addChooseCourse(String userId, Long courseId);

    XcCourseTablesDto getLearnStatus(String userId, Long courseId);
}
