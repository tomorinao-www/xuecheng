package link.tomorinao.xuecheng.content.service.impl;

import cn.hutool.core.io.IoUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import freemarker.template.Configuration;
import freemarker.template.Template;
import link.tomorinao.xuecheng.content.config.MultipartSupportConfig;
import link.tomorinao.xuecheng.content.feign.MediaServiceClient;
import link.tomorinao.xuecheng.messagesdk.model.po.MqMessage;
import link.tomorinao.xuecheng.messagesdk.service.MqMessageService;
import link.tomorinao.xuecheng.base.exception.CommonError;
import link.tomorinao.xuecheng.base.exception.XueChengException;
import link.tomorinao.xuecheng.base.utils.BeanUtils;
import link.tomorinao.xuecheng.content.mapper.CourseInfoMapper;
import link.tomorinao.xuecheng.content.mapper.CoursePublishMapper;
import link.tomorinao.xuecheng.content.mapper.CoursePublishPreMapper;
import link.tomorinao.xuecheng.content.model.dto.CourseInfoDto;
import link.tomorinao.xuecheng.content.model.dto.CoursePreviewDto;
import link.tomorinao.xuecheng.content.model.dto.TeachplanDto;
import link.tomorinao.xuecheng.content.model.po.CourseInfo;
import link.tomorinao.xuecheng.content.model.po.CoursePublish;
import link.tomorinao.xuecheng.content.model.po.CoursePublishPre;
import link.tomorinao.xuecheng.content.service.ICourseInfoService;
import link.tomorinao.xuecheng.content.service.ICoursePublishService;
import link.tomorinao.xuecheng.content.service.ITeachplanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 课程发布 服务实现类
 * </p>
 *
 * @author tomorinao
 * @since 2023-10-04
 */
@Service
@Slf4j
public class CoursePublishServiceImpl extends ServiceImpl<CoursePublishMapper, CoursePublish> implements ICoursePublishService {

    private ICourseInfoService iCourseInfoService;

    private ITeachplanService iTeachplanService;

    private CoursePublishPreMapper coursePublishPreMapper;
    private CoursePublishMapper coursePublishMapper;
    private CourseInfoMapper courseInfoMapper;
    private MqMessageService mqMessageService;
    private MediaServiceClient mediaServiceClient;


    @Autowired
    public CoursePublishServiceImpl(ICourseInfoService iCourseInfoService, ITeachplanService iTeachplanService, CoursePublishPreMapper coursePublishPreMapper, CoursePublishMapper coursePublishMapper, CourseInfoMapper courseInfoMapper, MqMessageService mqMessageService, MediaServiceClient mediaServiceClient) {
        this.iCourseInfoService = iCourseInfoService;
        this.iTeachplanService = iTeachplanService;
        this.coursePublishPreMapper = coursePublishPreMapper;
        this.coursePublishMapper = coursePublishMapper;
        this.courseInfoMapper = courseInfoMapper;
        this.mqMessageService = mqMessageService;
        this.mediaServiceClient = mediaServiceClient;
    }

    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        CourseInfoDto courseInfoDto = iCourseInfoService.getById(courseId);
        coursePreviewDto.setCourseInfoDto(courseInfoDto);
        List<TeachplanDto> teachplanDtoList = iTeachplanService.getTreeNodes(courseId);
        coursePreviewDto.setTeachplans(teachplanDtoList);
        return coursePreviewDto;
    }

    @Transactional
    @Override
    public void publishCourse(Long companyId, Long courseId) {
        // 1. 约束校验
        // 1.1 获取课程预发布表数据
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPre == null) {
            XueChengException.cast("请先提交课程审核，审核通过后方可发布");
        }
        // 1.2 课程审核通过后，方可发布
        if (!"202004".equals(coursePublishPre.getStatus())) {
            XueChengException.cast("操作失败，课程审核通过后方可发布");
        }
        // 1.3 本机构只允许发布本机构的课程
        if (!coursePublishPre.getCompanyId().equals(companyId)) {
            XueChengException.cast("操作失败，本机构只允许发布本机构的课程");
        }
        // 2. 向课程发布表插入数据
        saveCoursePublish(courseId);
        // 3. 向消息表插入数据
        saveCoursePublishMessage(courseId);
        // 4. 删除课程预发布表对应记录
        coursePublishPreMapper.deleteById(courseId);
    }

    @Override
    public File generateCourseHtml(Long courseId) throws Exception {
        // 1. 创建一个Freemarker配置
        Configuration configuration = new Configuration(Configuration.getVersion());
        // 2. 告诉Freemarker在哪里可以找到模板文件
        String classPath = this.getClass().getResource("/").getPath();
        configuration.setDirectoryForTemplateLoading(new File(classPath + "/templates/"));
        configuration.setDefaultEncoding("utf-8");
        // 3. 创建一个模型数据，与模板文件中的数据模型保持一致，这里是CoursePreviewDto类型
        CoursePreviewDto coursePreviewDto = this.getCoursePreviewInfo(courseId);
        HashMap<String, Object> map = new HashMap<>();
        map.put("model", coursePreviewDto);
        // 4. 加载模板文件
        Template template = configuration.getTemplate("course_template.ftl");
        // 5. 将数据模型应用于模板
        String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
        // 5.1 将静态文件内容输出到文件中
        File htmlFile = File.createTempFile("xuecheng/content/course/" + courseId, ".html");
        InputStream inputStream = IoUtil.toStream(content, StandardCharsets.UTF_8);
        FileOutputStream fileOutputStream = new FileOutputStream(htmlFile);
        IoUtil.copy(inputStream, fileOutputStream);
        return htmlFile;
    }

    @Override
    public void uploadCourseHtml(Long courseId, File file) {
        try {
            MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
            String course = mediaServiceClient.upload(multipartFile, "course", "course/" + courseId + ".html");
            if (course == null) {
                XueChengException.cast("远程调用媒资服务上传文件失败");
            }
        } catch (IOException e) {
            log.error("文件转MultipartFile失败;courseId: {};{}", courseId, e.getMessage());
        }
    }

    /**
     * 保存课程发布信息
     *
     * @param courseId 课程id
     */
    private void saveCoursePublish(Long courseId) {
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPre == null) {
            XueChengException.cast("课程预发布数据为空");
        }
        CoursePublish coursePublish = new CoursePublish();
        BeanUtils.copyProperties(coursePublishPre, coursePublish);
        // 设置发布状态为已发布
        coursePublish.setStatus("203002");
        CoursePublish coursePublishUpdate = coursePublishMapper.selectById(courseId);
        // 有则更新，无则新增
        if (coursePublishUpdate == null) {
            coursePublishMapper.insert(coursePublish);
        } else {
            coursePublishMapper.updateById(coursePublish);
        }
        // 更新课程基本信息表的发布状态为已发布
        CourseInfo courseInfo = new CourseInfo();
        courseInfo.setId(courseId);
        courseInfo.setAuditStatus("203002");
        courseInfoMapper.updateById(courseInfo);
    }

    /**
     * 保存消息表
     *
     * @param courseId 课程id
     */
    private void saveCoursePublishMessage(Long courseId) {
        MqMessage mqMessage = mqMessageService.addMessage("course_publish",
                String.valueOf(courseId), null, null);
        if (mqMessage == null) {
            XueChengException.cast(CommonError.UNKOWN_ERROR);
        }
    }
}
