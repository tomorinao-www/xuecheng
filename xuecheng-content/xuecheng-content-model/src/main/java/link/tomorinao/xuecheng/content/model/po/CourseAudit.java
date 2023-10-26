package link.tomorinao.xuecheng.content.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author tomorinao
 * @since 2023-10-04
 */
@Data
@TableName("course_audit")
public class CourseAudit implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 课程id
     */
    private Long courseId;

    /**
     * 审核意见
     */
    private String auditMind;

    /**
     * 审核状态
     */
    private String auditStatus;

    /**
     * 审核人
     */
    private String auditPeople;

    /**
     * 审核时间
     */
    private LocalDateTime auditDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getAuditMind() {
        return auditMind;
    }

    public void setAuditMind(String auditMind) {
        this.auditMind = auditMind;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getAuditPeople() {
        return auditPeople;
    }

    public void setAuditPeople(String auditPeople) {
        this.auditPeople = auditPeople;
    }

    public LocalDateTime getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(LocalDateTime auditDate) {
        this.auditDate = auditDate;
    }

    @Override
    public String toString() {
        return "CourseAudit{" +
            "id = " + id +
            ", courseId = " + courseId +
            ", auditMind = " + auditMind +
            ", auditStatus = " + auditStatus +
            ", auditPeople = " + auditPeople +
            ", auditDate = " + auditDate +
        "}";
    }
}
