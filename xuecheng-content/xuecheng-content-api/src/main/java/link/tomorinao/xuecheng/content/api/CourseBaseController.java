package link.tomorinao.xuecheng.content.api;


import jakarta.annotation.Resource;
import link.tomorinao.xuecheng.base.model.PageParams;
import link.tomorinao.xuecheng.base.model.PageResult;
import link.tomorinao.xuecheng.common.security.utils.SecurityUtil;
import link.tomorinao.xuecheng.content.model.dto.QueryCourseParamsDto;
import link.tomorinao.xuecheng.content.model.po.CourseBase;
import link.tomorinao.xuecheng.content.service.ICourseBaseService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/course")
public class CourseBaseController {
    @Resource
    ICourseBaseService iCourseBaseService;

    @PostMapping("/list")
    public PageResult<CourseBase> list(PageParams pageParams,
                                       @RequestBody(required = false) QueryCourseParamsDto queryCourseParamsDto) {
        PageResult<CourseBase> pageRes = iCourseBaseService.list(pageParams, queryCourseParamsDto);
        System.out.println("SecurityUtil.getUser() = " + SecurityUtil.getUser());
        return pageRes;
    }
//    @PostMapping
//    public CourseBaseInfoDto add(@RequestBody @Validated CourseInfoDto dto){
//        return iCourseBaseService.add(1232141425L, dto);
//    }

//    @GetMapping("/{id}")
//    public CourseBaseInfoDto getById(@PathVariable Long id){
//        return iCourseBaseService.getById(id);
//    }
}
