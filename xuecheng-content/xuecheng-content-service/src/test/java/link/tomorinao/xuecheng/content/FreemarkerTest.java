package link.tomorinao.xuecheng.content;

import cn.hutool.core.io.IoUtil;
import cn.hutool.extra.template.TemplateException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import link.tomorinao.xuecheng.content.model.dto.CoursePreviewDto;
import link.tomorinao.xuecheng.content.service.ICoursePublishService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@SpringBootTest
public class FreemarkerTest {
    @Autowired
    ICoursePublishService iCoursePublishService;

    @Test
    public void testGenerateHtmlByTemplate() throws Exception {
        // 1. 创建一个FreeMarker配置：
        Configuration configuration = new Configuration(Configuration.getVersion());
        // 2. 告诉FreeMarker在哪里可以找到模板文件。
        String classpath = FreemarkerTest.class.getResource("/").getPath();
        configuration.setDirectoryForTemplateLoading(new File("E:\\work\\idea\\xuecheng\\xuecheng-content\\xuecheng-content-service\\src\\test\\resources\\templates\\"));
        // 2.1 指定字符编码
        configuration.setDefaultEncoding("utf-8");
        // 3. 创建一个数据模型，与模板文件中的数据模型类型保持一致，这里是CoursePreviewDto类型
        CoursePreviewDto coursePreviewDto = iCoursePublishService.getCoursePreviewInfo(2L);
        HashMap<String, Object> map = new HashMap<>();
        map.put("model", coursePreviewDto);
        // 4. 加载模板文件
        Template template = configuration.getTemplate("course_template.ftl");
        // 5. 将数据模型应用于模板
        String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
        InputStream inputStream = IoUtil.toStream(content, StandardCharsets.UTF_8);
        FileOutputStream fileOutputStream = new FileOutputStream("E:\\work\\mytmp\\freemarker-test.html");
        IoUtil.copy(inputStream,fileOutputStream);
    }

}
