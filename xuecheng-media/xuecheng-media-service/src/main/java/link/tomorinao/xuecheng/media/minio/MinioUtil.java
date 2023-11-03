package link.tomorinao.xuecheng.media.minio;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public class MinioUtil {
    private MinioConfig minioConfig;
    private MinioClient minioClient;
    private String endpoint;
    // 公开存储桶，方便调用方查看桶、选择桶
    public MinioConfig.Bucket bucket;


    public MinioUtil(MinioConfig minioConfig) {
        this.minioConfig = minioConfig;
        refresh();
    }

    /*
    配置属性在刷新完成之后，会发送一个RefreshScopeRefreshedEvent事件，
    监听这个事件，重新构造MinioClient
     */
    @EventListener(RefreshScopeRefreshedEvent.class)
    public void refresh() {
        this.endpoint = minioConfig.getEndpoint();
        this.bucket = minioConfig.getBucket();
        this.minioClient = MinioClient.builder()
                .endpoint(this.endpoint)
                .credentials(minioConfig.getAccessKey(), minioConfig.getSecretKey())
                .build();
    }

    public String upload(MultipartFile file, String objectName, String bucket) throws Exception {
        // 获取上传的文件的输入流
        InputStream inputStream = file.getInputStream();

        //上传文件到 minio
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucket)
                .object(objectName)
                .contentType(file.getContentType())
                .stream(inputStream, inputStream.available(), -1)
                .build());
        //文件访问路径
        String url = endpoint + "/" + bucket + "/" + objectName;
        return url;
    }
}
