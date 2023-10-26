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
@TableName("teachplan_work")
@Data
public class TeachplanWork implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 作业信息标识
     */
    private Long workId;

    /**
     * 作业标题
     */
    private String workTitle;

    /**
     * 课程计划标识
     */
    private Long teachplanId;

    /**
     * 课程标识
     */
    private Long courseId;

    private LocalDateTime createDate;

    private Long coursePubId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWorkId() {
        return workId;
    }

    public void setWorkId(Long workId) {
        this.workId = workId;
    }

    public String getWorkTitle() {
        return workTitle;
    }

    public void setWorkTitle(String workTitle) {
        this.workTitle = workTitle;
    }

    public Long getTeachplanId() {
        return teachplanId;
    }

    public void setTeachplanId(Long teachplanId) {
        this.teachplanId = teachplanId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public Long getCoursePubId() {
        return coursePubId;
    }

    public void setCoursePubId(Long coursePubId) {
        this.coursePubId = coursePubId;
    }

    @Override
    public String toString() {
        return "TeachplanWork{" +
            "id = " + id +
            ", workId = " + workId +
            ", workTitle = " + workTitle +
            ", teachplanId = " + teachplanId +
            ", courseId = " + courseId +
            ", createDate = " + createDate +
            ", coursePubId = " + coursePubId +
        "}";
    }
}
