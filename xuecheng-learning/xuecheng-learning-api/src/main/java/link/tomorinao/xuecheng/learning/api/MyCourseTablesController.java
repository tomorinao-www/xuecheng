package link.tomorinao.xuecheng.learning.api;

import cn.hutool.core.util.StrUtil;
import link.tomorinao.xuecheng.base.exception.XueChengException;
import link.tomorinao.xuecheng.base.model.PageResult;
import link.tomorinao.xuecheng.common.security.utils.SecurityUtil;
import link.tomorinao.xuecheng.learning.model.dto.MyCourseTableParams;
import link.tomorinao.xuecheng.learning.model.dto.XcChooseCourseDto;
import link.tomorinao.xuecheng.learning.model.dto.XcCourseTablesDto;
import link.tomorinao.xuecheng.learning.model.po.XcCourseTables;

import link.tomorinao.xuecheng.learning.service.MyCourseTablesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mr.M
 * @version 1.0
 * @description 我的课程表接口
 * @date 2022/10/25 9:40
 */


@Slf4j
@RestController
public class MyCourseTablesController {


    @Autowired
    MyCourseTablesService myCourseTablesService;

    @PostMapping("/choosecourse/{courseId}")
    public XcChooseCourseDto addChooseCourse(@PathVariable("courseId") Long courseId) {
        SecurityUtil.XcUser xcUser = SecurityUtil.getUser();
        if (xcUser == null) {
            XueChengException.cast("请登录");
        }
        String userId = xcUser.getId();
        return myCourseTablesService.addChooseCourse(userId, courseId);
    }


    @PostMapping("/choosecourse/learnstatus/{courseId}")
    public XcCourseTablesDto getLearnStatus(@PathVariable("courseId") Long courseId) {

        SecurityUtil.XcUser xcUser = SecurityUtil.getUser();
        if (xcUser == null) {
            XueChengException.cast("请登录");
        }
        String userId = xcUser.getId();

        return myCourseTablesService.getLearnStatus(userId, courseId);

    }


    @GetMapping("/mycoursetable")
    public PageResult<XcCourseTables> mycoursetable(MyCourseTableParams params) {
        return null;
    }

}
