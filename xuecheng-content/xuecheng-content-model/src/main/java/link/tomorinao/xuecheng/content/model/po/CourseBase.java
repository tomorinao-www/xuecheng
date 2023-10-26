package link.tomorinao.xuecheng.content.model.po;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import link.tomorinao.xuecheng.base.exception.ValidationGroup;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 课程基本信息
 * </p>
 *
 * @author tomorinao
 * @since 2023-10-04
 */
@Data
@TableName("course_info")
public class CourseBase implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @NotEmpty(message = "修改必须指定id", groups = {ValidationGroup.Update.class})
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 机构ID
     */
    private Long companyId;

    /**
     * 机构名称
     */
    private String companyName;

    /**
     * 课程名称
     */
    @NotEmpty(message = "课程名称不能为空", groups = {ValidationGroup.Insert.class})
    private String name;

    /**
     * 适用人群
     */
    @NotEmpty(message = "适用人群不能为空")
    private String users;

    /**
     * 课程标签
     */
    private String tags;

    /**
     * 大分类
     */
    @NotEmpty(message = "课程分类不能为空")
    private String mt;

    /**
     * 小分类
     */
    @NotEmpty(message = "课程分类不能为空")
    private String st;

    /**
     * 课程等级
     */
    @NotEmpty(message = "课程等级不能为空")
    private String grade;

    /**
     * 教育模式(common普通，record 录播，live直播等）
     */
    private String teachmode;

    /**
     * 课程介绍
     */
    @Size(message = "课程介绍内容过少", min = 10)
    private String description;

    /**
     * 课程图片
     */
    private String pic;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createDate;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime changeDate;

    /**
     * 创建人
     */
    private String createPeople;

    /**
     * 更新人
     */
    private String changePeople;

    /**
     * 审核状态
     */
    @TableField(fill = FieldFill.INSERT)
    private String auditStatus;

    /**
     * 课程发布状态 未发布  已发布 下线
     */
    @TableField(fill = FieldFill.INSERT)
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getMt() {
        return mt;
    }

    public void setMt(String mt) {
        this.mt = mt;
    }

    public String getSt() {
        return st;
    }

    public void setSt(String st) {
        this.st = st;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getTeachmode() {
        return teachmode;
    }

    public void setTeachmode(String teachmode) {
        this.teachmode = teachmode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(LocalDateTime changeDate) {
        this.changeDate = changeDate;
    }

    public String getCreatePeople() {
        return createPeople;
    }

    public void setCreatePeople(String createPeople) {
        this.createPeople = createPeople;
    }

    public String getChangePeople() {
        return changePeople;
    }

    public void setChangePeople(String changePeople) {
        this.changePeople = changePeople;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CourseBase{" +
            "id = " + id +
            ", companyId = " + companyId +
            ", companyName = " + companyName +
            ", name = " + name +
            ", users = " + users +
            ", tags = " + tags +
            ", mt = " + mt +
            ", st = " + st +
            ", grade = " + grade +
            ", teachmode = " + teachmode +
            ", description = " + description +
            ", pic = " + pic +
            ", createDate = " + createDate +
            ", changeDate = " + changeDate +
            ", createPeople = " + createPeople +
            ", changePeople = " + changePeople +
            ", auditStatus = " + auditStatus +
            ", status = " + status +
        "}";
    }
}
