package link.tomorinao.xuecheng.base.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 填充器
 *
 */
@Slf4j
@Component
public class DateTimeHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert fill ....");
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.setFieldValByName("operateTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("createDate", LocalDateTime.now(), metaObject);
        this.setFieldValByName("changeDate", LocalDateTime.now(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update fill ....");
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("operateTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("createDate", LocalDateTime.now(), metaObject);
        this.setFieldValByName("changeDate", LocalDateTime.now(), metaObject);

    }
}