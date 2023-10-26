package link.tomorinao.xuecheng.content.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 课程分类
 * </p>
 *
 * @author tomorinao
 * @since 2023-10-04
 */
@Data
@TableName("course_category")
public class CourseCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private String id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类标签默认和名称一样
     */
    private String label;

    /**
     * 父结点id（第一级的父节点是0，自关联字段id）
     */
    private String parentid;

    /**
     * 是否显示
     */
    private Byte isShow;

    /**
     * 排序字段
     */
    private Integer orderby;

    /**
     * 是否叶子
     */
    private Byte isLeaf;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public Byte getIsShow() {
        return isShow;
    }

    public void setIsShow(Byte isShow) {
        this.isShow = isShow;
    }

    public Integer getOrderby() {
        return orderby;
    }

    public void setOrderby(Integer orderby) {
        this.orderby = orderby;
    }

    public Byte getIsLeaf() {
        return isLeaf;
    }

    public void setIsLeaf(Byte isLeaf) {
        this.isLeaf = isLeaf;
    }

    @Override
    public String toString() {
        return "CourseCategory{" +
            "id = " + id +
            ", name = " + name +
            ", label = " + label +
            ", parentid = " + parentid +
            ", isShow = " + isShow +
            ", orderby = " + orderby +
            ", isLeaf = " + isLeaf +
        "}";
    }
}
