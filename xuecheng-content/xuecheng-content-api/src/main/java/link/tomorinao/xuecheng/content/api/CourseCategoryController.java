package link.tomorinao.xuecheng.content.api;

import jakarta.annotation.Resource;
import link.tomorinao.xuecheng.content.model.dto.CourseCategoryTreeDto;
import link.tomorinao.xuecheng.content.service.ICourseCategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CourseCategoryController {

    @Resource
    private ICourseCategoryService iCourseCategoryService;

    @GetMapping("/course-category/tree-nodes")
    public List<CourseCategoryTreeDto> querryTreeNodes(String id){
        return iCourseCategoryService.queryTreeNodes("1");
    }
}
