package link.tomorinao.xuecheng.media.minio;

import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import jakarta.annotation.Resource;
import link.tomorinao.xuecheng.media.service.MediaFileService;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public class MinioUtil {

    private MinioConfig minioConfig;

    @Getter
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

    public String upload(MultipartFile file, String objectPath, String bucket) throws Exception {
        // 获取上传的文件的输入流
        InputStream inputStream = file.getInputStream();

        //上传文件到 minio
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucket)
                .object(objectPath)
                .contentType(file.getContentType())
                .stream(inputStream, inputStream.available(), -1)
                .build());
        //文件访问路径
        String url = endpoint + "/" + bucket + "/" + objectPath;
        return url;
    }

    public String upload(File file, String objectPath, String bucket) throws Exception {
        //上传文件到 minio
        minioClient.uploadObject(UploadObjectArgs.builder()
                .bucket(bucket)
                .filename(file.getAbsolutePath())
                .object(objectPath)
                .build());
        //文件访问路径
        String url = endpoint + "/" + bucket + "/" + objectPath;
        return url;
    }

    public void compose(List<String> srcPathList, String objectPath, String bucket) throws Exception {
        List<ComposeSource> srcList = srcPathList.stream()
                .map(o -> ComposeSource.builder()
                        .bucket(bucket)
                        .object(o)
                        .build())
                .toList();

        minioClient.composeObject(ComposeObjectArgs.builder()
                .bucket(bucket)
                .sources(srcList)
                .object(objectPath)
                .build());
    }

    public StatObjectResponse statObject(String objectPath, String bucket) throws Exception {
        StatObjectResponse stat = minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectPath)
                        .build());
        return stat;
    }

    public void remove(String object, String bucket) throws Exception {
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(bucket)
                .object(object)
                .build());
    }

    public Iterable<Result<DeleteError>> remove(List<String> objectPathList, String bucket) throws Exception {
        List<DeleteObject> objects = objectPathList.stream().map(DeleteObject::new).toList();
        Iterable<Result<DeleteError>> results = minioClient.removeObjects(RemoveObjectsArgs.builder()
                .bucket(bucket)
                .objects(objects)
                .build());
        return results;
    }

    public InputStream getObject(String objectPath, String bucket) throws Exception {
        InputStream inputStream = minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucket)
                .object(objectPath)
                .build());
        return inputStream;
    }


    @NotNull
    public static String getParentPath(String md5) {
        return md5.charAt(0) + "/" + md5.charAt(1) + "/" + md5;
    }

    @NotNull
    public static String getObjectPath(String md5, String ext) {
        return getParentPath(md5) + "/" + md5 + "." + ext;
    }

    @NotNull
    public static String getChunkPath(String md5, int chunkIndex) {
        return getParentPath(md5) + "/chunk/" + chunkIndex;
    }
}
