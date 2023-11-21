package link.tomorinao.xuecheng.content.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class CoursePreviewDto {

    /**
     * 课程基本计划、课程营销信息
     */
    CourseInfoDto courseInfoDto;

    /**
     * 课程计划信息
     */
    List<TeachplanDto> teachplans;

    /**
     * 师资信息暂时不加
     */

}