package link.tomorinao.xuecheng.content.jobhandler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import link.tomorinao.xuecheng.base.exception.XueChengException;
import link.tomorinao.xuecheng.base.utils.BeanUtils;
import link.tomorinao.xuecheng.content.feign.search.SearchServiceClient;
import link.tomorinao.xuecheng.content.feign.search.po.CourseIndex;
import link.tomorinao.xuecheng.content.model.po.CoursePublish;
import link.tomorinao.xuecheng.content.service.ICoursePublishService;
import link.tomorinao.xuecheng.messagesdk.model.po.MqMessage;
import link.tomorinao.xuecheng.messagesdk.service.MessageProcessAbstract;
import link.tomorinao.xuecheng.messagesdk.service.MqMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Slf4j
public class CoursePublishTask extends MessageProcessAbstract {

    private MqMessageService mqMessageService;
    private ICoursePublishService iCoursePublishService;

    private SearchServiceClient searchServiceClient;

    @Autowired
    public CoursePublishTask(MqMessageService mqMessageService, ICoursePublishService iCoursePublishService, SearchServiceClient searchServiceClient) {
        this.mqMessageService = mqMessageService;
        this.iCoursePublishService = iCoursePublishService;
        this.searchServiceClient = searchServiceClient;
    }


    @XxlJob("coursePublishJobHandler")
    public void coursePublishJobHandler() {
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();//执行器的序号，从0开始
        int shardTotal = XxlJobHelper.getShardTotal();//执行器总数
        process(shardIndex, shardTotal, "course_publish", 50, 60);

    }


    @Override
    public boolean execute(MqMessage mqMessage) {
        log.info("开始执行课程发布任务 mqid: {}", mqMessage.getId());
        // 拿出课程id
        Long courseId = Long.valueOf(mqMessage.getBusinessKey1());
        // 1阶段：静态化页面上传minio
        generateCourseHtml(mqMessage, courseId);
        // 2阶段：写elastic search
        saveCourseIndex(mqMessage, courseId);
        // 3阶段：写redis
        return false;
    }


    private void generateCourseHtml(MqMessage mqMessage, Long courseId) {
        // 1.幂等性判断
        Long mqId = mqMessage.getId();
        int stageOne = mqMessageService.getStageOne(mqId);
        if (stageOne == 1) {
            log.debug("已完成静态页面生成");
            return;
        }
        // 2. 生成静态页面
        try {
            File file = iCoursePublishService.generateCourseHtml(courseId);
            // 3. 将静态页面上传至MinIO
            iCoursePublishService.uploadCourseHtml(courseId, file);
        } catch (Exception e) {
            log.error("课程静态化异常;mqId->{}", mqId);
            XueChengException.cast("课程静态化异常");
        }
        // 4. 保存第一阶段状态
        mqMessageService.completedStageOne(mqId);
    }


    private void saveCourseIndex(MqMessage mqMessage, Long courseId) {
        // 1.幂等性判断
        Long mqId = mqMessage.getId();
        int stageOne = mqMessageService.getStageTwo(mqId);
        if (stageOne == 1) {
            log.debug("已完成添加课程索引，无需重复添加;mqId->{}", mqId);
            return;
        }
        // 2.查课程信息
        CoursePublish coursePublish = iCoursePublishService.getById(courseId);
        CourseIndex courseIndex = new CourseIndex();
        BeanUtils.copyPropertiesIgnoreNull(coursePublish, courseIndex);
        // 3..远程调用写es
        Boolean add_b = searchServiceClient.add(courseIndex);
        if (!add_b) {
            log.error("远程调用写es失败;mqId->{}", mqId);
            XueChengException.cast("远程调用写es失败");
        }
        // 4. 保存第一阶段状态
        mqMessageService.completedStageTwo(mqId);
    }
}
