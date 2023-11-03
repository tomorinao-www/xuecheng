package link.tomorinao.xuecheng.media.minio;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MinioConfig.class)
public class MinioAutoConfiguration {
    @Bean
    public MinioUtil minioUtil(MinioConfig minioConfig) {
        return new MinioUtil(minioConfig);
    }
}
