package link.tomorinao.xuecheng.common.security.utils;

import com.alibaba.fastjson2.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.io.Serializable;
import java.time.LocalDateTime;


@Slf4j
public class SecurityUtil {
    public static XcUser getUser() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if(principal instanceof Jwt jwt){
                return JSON.parseObject(jwt.getSubject(), XcUser.class);
            }
        } catch (Exception e) {
            log.error("获取当前登录用户身份信息出错：{}", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * <p>
     *
     * </p>
     *
     * @author itcast
     */
    @Data
    public static class XcUser implements Serializable {

        private static final long serialVersionUID = 1L;

        private String id;

        private String username;

        private String password;

        private String salt;

        private String name;
        private String nickname;
        private String wxUnionid;
        private String companyId;
        /**
         * 头像
         */
        private String userpic;

        private String utype;

        private LocalDateTime birthday;

        private String sex;

        private String email;

        private String cellphone;

        private String qq;

        /**
         * 用户状态
         */
        private String status;

        private LocalDateTime createTime;

        private LocalDateTime updateTime;


    }
}