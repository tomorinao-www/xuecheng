package link.tomorinao.xuecheng.content.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 课程发布
 * </p>
 *
 * @author tomorinao
 * @since 2023-10-04
 */
@Data
@TableName("course_publish")
public class CoursePublish implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 机构ID
     */
    private Long companyId;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 课程名称
     */
    private String name;

    /**
     * 适用人群
     */
    private String users;

    /**
     * 标签
     */
    private String tags;

    /**
     * 创建人
     */
    private String username;

    /**
     * 大分类
     */
    private String mt;

    /**
     * 大分类名称
     */
    private String mtName;

    /**
     * 小分类
     */
    private String st;

    /**
     * 小分类名称
     */
    private String stName;

    /**
     * 课程等级
     */
    private String grade;

    /**
     * 教育模式
     */
    private String teachmode;

    /**
     * 课程图片
     */
    private String pic;

    /**
     * 课程介绍
     */
    private String description;

    /**
     * 课程营销信息，json格式
     */
    private String market;

    /**
     * 所有课程计划，json格式
     */
    private String teachplan;

    /**
     * 教师信息，json格式
     */
    private String teachers;

    /**
     * 发布时间
     */
    private LocalDateTime createDate;

    /**
     * 上架时间
     */
    private LocalDateTime onlineDate;

    /**
     * 下架时间
     */
    private LocalDateTime offlineDate;

    /**
     * 发布状态
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 收费规则，对应数据字典--203
     */
    private String charge;

    /**
     * 现价
     */
    private Float price;

    /**
     * 原价
     */
    private Double originalPrice;

    /**
     * 课程有效期天数
     */
    private Integer validDays;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMt() {
        return mt;
    }

    public void setMt(String mt) {
        this.mt = mt;
    }

    public String getMtName() {
        return mtName;
    }

    public void setMtName(String mtName) {
        this.mtName = mtName;
    }

    public String getSt() {
        return st;
    }

    public void setSt(String st) {
        this.st = st;
    }

    public String getStName() {
        return stName;
    }

    public void setStName(String stName) {
        this.stName = stName;
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

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getTeachplan() {
        return teachplan;
    }

    public void setTeachplan(String teachplan) {
        this.teachplan = teachplan;
    }

    public String getTeachers() {
        return teachers;
    }

    public void setTeachers(String teachers) {
        this.teachers = teachers;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getOnlineDate() {
        return onlineDate;
    }

    public void setOnlineDate(LocalDateTime onlineDate) {
        this.onlineDate = onlineDate;
    }

    public LocalDateTime getOfflineDate() {
        return offlineDate;
    }

    public void setOfflineDate(LocalDateTime offlineDate) {
        this.offlineDate = offlineDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Integer getValidDays() {
        return validDays;
    }

    public void setValidDays(Integer validDays) {
        this.validDays = validDays;
    }

    @Override
    public String toString() {
        return "CoursePublish{" +
            "id = " + id +
            ", companyId = " + companyId +
            ", companyName = " + companyName +
            ", name = " + name +
            ", users = " + users +
            ", tags = " + tags +
            ", username = " + username +
            ", mt = " + mt +
            ", mtName = " + mtName +
            ", st = " + st +
            ", stName = " + stName +
            ", grade = " + grade +
            ", teachmode = " + teachmode +
            ", pic = " + pic +
            ", description = " + description +
            ", market = " + market +
            ", teachplan = " + teachplan +
            ", teachers = " + teachers +
            ", createDate = " + createDate +
            ", onlineDate = " + onlineDate +
            ", offlineDate = " + offlineDate +
            ", status = " + status +
            ", remark = " + remark +
            ", charge = " + charge +
            ", price = " + price +
            ", originalPrice = " + originalPrice +
            ", validDays = " + validDays +
        "}";
    }
}
