package link.tomorinao.xuecheng.search.controller;

import link.tomorinao.xuecheng.base.model.PageParams;
import link.tomorinao.xuecheng.base.model.PageResult;
import link.tomorinao.xuecheng.search.dto.SearchCourseParamDto;
import link.tomorinao.xuecheng.search.dto.SearchPageResultDto;
import link.tomorinao.xuecheng.search.po.CourseIndex;
import link.tomorinao.xuecheng.search.service.CourseSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Mr.M
 * @version 1.0
 * @description 课程搜索接口
 * @date 2022/9/24 22:31
 */
@RestController
@RequestMapping("/course")
public class CourseSearchController {

    @Autowired
    CourseSearchService courseSearchService;


    @GetMapping("/list")
    public SearchPageResultDto<CourseIndex> list(PageParams pageParams, SearchCourseParamDto searchCourseParamDto) {

        return courseSearchService.queryCoursePubIndex(pageParams, searchCourseParamDto);

    }
}
