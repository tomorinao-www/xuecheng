package link.tomorinao.xuecheng.content.config;

import cn.hutool.core.io.IoUtil;
import feign.codec.Encoder;
import feign.form.ContentType;
import feign.form.spring.SpringFormEncoder;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Configuration
public class MultipartSupportConfig {

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    @Bean
    @Primary//注入相同类型的bean时优先使用
    @Scope("prototype")
    public Encoder feignEncoder() {
        return new SpringFormEncoder(new SpringEncoder(messageConverters));
    }

    //将file转为Multipart
    public static MultipartFile getMultipartFile(File file) throws IOException {
//        FileItem item = new DiskFileItemFactory().createItem("file", MediaType.MULTIPART_FORM_DATA_VALUE, true, file.getName());
//        try (FileInputStream inputStream = new FileInputStream(file);
//             OutputStream outputStream = item.getOutputStream();) {
//            IoUtil.copy(inputStream, outputStream);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return new CommonsMultipartFile(item); // spring6删除了CommonsMultipartFile
        FileInputStream inputStream = new FileInputStream(file);
        return new MockMultipartFile(file.getName(), file.getName(), MediaType.MULTIPART_FORM_DATA_VALUE,inputStream);
    }
}
