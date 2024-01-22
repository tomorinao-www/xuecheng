package link.tomorinao.xuecheng.content.api;


import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import link.tomorinao.xuecheng.common.security.utils.SecurityUtil;
import link.tomorinao.xuecheng.content.model.dto.CourseInfoDto;
import link.tomorinao.xuecheng.content.service.ICourseInfoService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/course")
public class CourseInfoController {
    @Resource
    ICourseInfoService iCourseInfoService;


    @GetMapping("/{id}")
    public CourseInfoDto getById(@PathVariable Long id) {
        return iCourseInfoService.getById(id);
    }

    @PostMapping
    public CourseInfoDto add(@RequestBody @Validated CourseInfoDto dto) {
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        Long companyId = null;
        if (StrUtil.isNotEmpty(user.getCompanyId())) {
            companyId = Long.valueOf(user.getCompanyId());
        }
        Long id = iCourseInfoService.add(companyId, dto);
        return iCourseInfoService.getById(id);
    }

    @PutMapping
    public CourseInfoDto update(@RequestBody @Validated CourseInfoDto dto) {
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        Long companyId = null;
        if (StrUtil.isNotEmpty(user.getCompanyId())) {
            companyId = Long.valueOf(user.getCompanyId());
        }
        Long id = iCourseInfoService.updateById(companyId, dto);
        return iCourseInfoService.getById(id);
    }
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        Long companyId = null;
        if (StrUtil.isNotEmpty(user.getCompanyId())) {
            companyId = Long.valueOf(user.getCompanyId());
        }
        iCourseInfoService.deleteById(companyId,id);
    }

}
