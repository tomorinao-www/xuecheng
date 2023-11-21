package link.tomorinao.xuecheng.content.feign;


import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class MediaServiceClientFallbackFactory implements FallbackFactory<MediaServiceClient> {
    @Override
    public MediaServiceClient create(Throwable throwable) {
        return new MediaServiceClient() {
            @Override
            public String upload(MultipartFile upload, String folder, String objectPath) {
                log.debug("方式二：熔断处理，熔断异常：{}", throwable.getMessage());
                return null;
            }
        };
    }
}