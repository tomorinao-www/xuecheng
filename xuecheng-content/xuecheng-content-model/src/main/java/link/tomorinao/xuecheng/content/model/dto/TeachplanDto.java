package link.tomorinao.xuecheng.content.model.dto;

import link.tomorinao.xuecheng.content.model.po.Teachplan;
import link.tomorinao.xuecheng.content.model.po.TeachplanMedia;
import lombok.Data;

import java.util.List;

@Data
public class TeachplanDto extends Teachplan {
    private TeachplanMedia teachplanMedia;

    private List<TeachplanDto> teachPlanTreeNodes;

}
