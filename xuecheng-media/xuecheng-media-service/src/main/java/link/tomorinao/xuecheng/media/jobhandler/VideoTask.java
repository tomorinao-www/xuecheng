package link.tomorinao.xuecheng.media.jobhandler;

import cn.hutool.core.io.file.FileNameUtil;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import link.tomorinao.xuecheng.base.utils.Mp4VideoUtil;
import link.tomorinao.xuecheng.media.mapper.MediaProcessMapper;
import link.tomorinao.xuecheng.media.minio.MinioUtil;
import link.tomorinao.xuecheng.media.model.po.MediaProcess;
import link.tomorinao.xuecheng.media.service.MediaProcessService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class VideoTask {

    private final MediaProcessService mediaProcessService;
    private final MinioUtil minioUtil;

    @Value("${ffmpeg.path}")
    private String ffmpeg_path;

    @Autowired
    public VideoTask(MediaProcessService mediaProcessService, MinioUtil minioUtil) {
        this.mediaProcessService = mediaProcessService;
        this.minioUtil = minioUtil;
    }

    /*
    视频转码任务处理
     */
    @XxlJob("videoJobHandler")
    public void videoJobHandler() {
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();//执行器的序号，从0开始
        int shardTotal = XxlJobHelper.getShardTotal();//执行器总数

        // CPU核心数
        int processors = Runtime.getRuntime().availableProcessors();

        // 查任务
        List<MediaProcess> mediaProcesseList = mediaProcessService.selectList(shardTotal, shardIndex, processors);
        int size = mediaProcesseList.size();
        log.info("查到了 {} 个任务", size);
        if (size <= 0) {
            return;
        }
        // 线程池执行任务
        ExecutorService executorService = Executors.newFixedThreadPool(size);
        // 任务计数器阻塞，等所有任务完成后再退出此方法
        CountDownLatch countDownLatch = new CountDownLatch(size);
        for (MediaProcess mediaProcess : mediaProcesseList) {
            executorService.execute(() -> {
                try {// 1.抢任务
                    Long tastId = mediaProcess.getId();
                    Boolean started = mediaProcessService.startTask(tastId);
                    // 没抢到
                    if (!started) {
                        log.info("抢占任务{}失败", tastId);
                        return;
                    }
                    // 抢到任务
                    // 2.下载视频到本地
                    // 创建源文件的临时文件
                    String fileId = mediaProcess.getFileId();
                    String objectPath = mediaProcess.getFilePath();
                    File srcTempFile;
                    try {
                        srcTempFile = createTempFile(fileId, "." + FileNameUtil.extName(objectPath));
                    } catch (Exception e) {
                        log.error("创建源文件的临时文件失败！", e);
                        return;
                    }
                    // 下载源文件
                    String bucket = mediaProcess.getBucket();
                    try (InputStream srcInputStream = minioUtil.getObject(objectPath, bucket);
                         OutputStream srcOutputStream = new FileOutputStream(srcTempFile)) {
                        IOUtils.copy(srcInputStream, srcOutputStream);
                    } catch (Exception e) {
                        mediaProcessService.saveProcessFinishStatus(tastId, "3", fileId, null, "下载视频到本地出错");
                        return;
                    }
                    // 3.创建转码目标mp4临时文件
                    File mp4TempFile;
                    try {
                        mp4TempFile = createTempFile(fileId, "mp4");
                    } catch (Exception e) {
                        log.error("创建转码目标mp4文件的临时文件失败！", e);
                        return;
                    }
                    // 4.开始转码
                    Mp4VideoUtil mp4VideoUtil = new Mp4VideoUtil(ffmpeg_path,
                            srcTempFile.getAbsolutePath(),
                            mp4TempFile.getName(),
                            mp4TempFile.getAbsolutePath());
                    String msg = mp4VideoUtil.generateMp4();
                    // 不管成功失败，都要删除源文件的临时文件
                    srcTempFile.delete();
                    // 转码失败
                    if (!"success".equals(msg)) {
                        log.error("转码失败 {}", msg);
                        mediaProcessService.saveProcessFinishStatus(tastId, "3", fileId, null, msg);
                        return;
                    }
                    // 转码成功
                    // 5.上传转码文件
                    String mp4objectPath = MinioUtil.getObjectPath(fileId, "mp4");
                    try {
                        minioUtil.upload(mp4TempFile, mp4objectPath, bucket);
                    } catch (Exception e) {
                        log.error("上传转码文件失败！", e);
                    } finally {
                        // 不管成功失败，都要删除mp4临时文件
                        mp4TempFile.delete();
                    }
                    // 6.转码任务成功记录
                    String url = "/" + bucket + "/" + mp4objectPath;
                    mediaProcessService.saveProcessFinishStatus(tastId, "2", fileId, url, "");
                } finally {
                    // 7.计数器减一
                    countDownLatch.countDown();
                }
            });
        }
        try {
            countDownLatch.await(size * 3L, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            log.debug("计数器打断异常", e);
        }
    }

    private File createTempFile(String filename, String ext) throws IOException {
        File file = File.createTempFile(filename, "." + ext);
        return file;
    }
}
