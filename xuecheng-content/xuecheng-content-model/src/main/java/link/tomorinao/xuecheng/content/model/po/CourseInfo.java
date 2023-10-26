package link.tomorinao.xuecheng.content.model.po;


import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("course_info")
public class CourseInfo extends CourseBase implements Serializable {

    /**
     * 收费规则，对应数据字典
     */
    @NotEmpty(message = "收费规则不能为空")
    private String charge;

    /**
     * 现价
     */
    private Double price;

    /**
     * 原价
     */
    private Double originalPrice;

    /**
     * 咨询qq
     */
    private String qq;

    /**
     * 微信
     */
    private String wechat;

    /**
     * 电话
     */
    private String phone;

    /**
     * 有效期天数
     */
    private Integer validDays;

    @Override
    public String toString() {
        return "CourseInfo{" +
                "charge='" + charge + '\'' +
                ", price=" + price +
                ", originalPrice=" + originalPrice +
                ", qq='" + qq + '\'' +
                ", wechat='" + wechat + '\'' +
                ", phone='" + phone + '\'' +
                ", validDays=" + validDays +
                '}';
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getValidDays() {
        return validDays;
    }

    public void setValidDays(Integer validDays) {
        this.validDays = validDays;
    }

}
