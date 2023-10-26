package link.tomorinao.xuecheng.content.handlers;

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
public class XCMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert fill ....");
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.setFieldValByName("operateTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("createDate", LocalDateTime.now(), metaObject);
        this.setFieldValByName("changeDate", LocalDateTime.now(), metaObject);
        // 审核状态默认 未提交
        this.strictInsertFill(metaObject, "auditStatus", String.class, "202002");
        // 发布状态默认 未发布
        this.strictInsertFill(metaObject, "status", String.class, "203001");

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