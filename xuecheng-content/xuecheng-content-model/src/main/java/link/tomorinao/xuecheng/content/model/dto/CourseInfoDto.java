package link.tomorinao.xuecheng.content.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import link.tomorinao.xuecheng.base.exception.ValidationGroup;
import lombok.Data;

/**
 * @author Mr.M
 * @version 1.0
 * @description 添加课程dto
 * @date 2022/9/7 17:40
 */
@Data
public class CourseInfoDto {

    @NotEmpty(message = "修改必须指定id", groups = {ValidationGroup.Update.class})
    private Long id;

    @NotEmpty(message = "课程名称不能为空", groups = {ValidationGroup.Insert.class})
    private String name;

    @NotEmpty(message = "适用人群不能为空")
    private String users;

    private String tags;

    @NotEmpty(message = "课程分类不能为空")
    private String mt;

    @NotEmpty(message = "课程分类不能为空")
    private String st;

    @NotEmpty(message = "课程等级不能为空")
    private String grade;

    private String teachmode;

    @Size(message = "课程描述内容过少", min = 10)
    private String description;

    private String pic;

    @NotEmpty(message = "收费规则不能为空")
    private String charge;

    private Double originalPrice;
    private Double price;


    private String qq;
    private String wechat;
    private String phone;

    private Integer validDays;

    private String mtName;
    private String stName;
}
