package link.tomorinao.xuecheng.content.mphandler;

import link.tomorinao.xuecheng.base.handler.DateTimeHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;


/**
 * 填充器
 */
@Slf4j
@Component
@Primary
public class ContentHandler extends DateTimeHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        super.insertFill(metaObject);
        // 审核状态默认 未提交
        this.strictInsertFill(metaObject, "auditStatus", String.class, "202002");
        // 发布状态默认 未发布
        this.strictInsertFill(metaObject, "status", String.class, "203001");

    }

    @Override
    public void updateFill(MetaObject metaObject) {

    }
}