package link.tomorinao.xuecheng.media.minio;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "minio")
@Data
public class MinioConfig {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private Bucket bucket;

    @Data
    public static class Bucket {
        private String media;
        private String video;
        private String test;
    }
}
