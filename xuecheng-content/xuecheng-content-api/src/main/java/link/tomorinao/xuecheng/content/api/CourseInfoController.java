package link.tomorinao.xuecheng.content.api;


import jakarta.annotation.Resource;
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
        Long id = iCourseInfoService.add(1232141425L, dto);
        return iCourseInfoService.getById(id);
    }

    @PutMapping
    public CourseInfoDto update(@RequestBody @Validated CourseInfoDto dto) {
        Long id = iCourseInfoService.updateById(1232141425L, dto);
        return iCourseInfoService.getById(id);
    }
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        Long companyId = 1232141425L;
        iCourseInfoService.deleteById(companyId,id);
    }

}
