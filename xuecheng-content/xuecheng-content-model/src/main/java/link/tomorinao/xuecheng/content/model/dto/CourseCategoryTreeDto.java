package link.tomorinao.xuecheng.content.model.dto;

import link.tomorinao.xuecheng.content.model.po.CourseCategory;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {
    private List<CourseCategoryTreeDto> childrenTreeNodes;

    public void addChild(CourseCategoryTreeDto node) {
        if(childrenTreeNodes==null){
            childrenTreeNodes = new ArrayList<>();
        }
        childrenTreeNodes.add(node);
    }
}
