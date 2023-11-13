package link.tomorinao.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import link.tomorinao.xuecheng.base.utils.BeanUtils;
import link.tomorinao.xuecheng.media.mapper.MediaFilesMapper;
import link.tomorinao.xuecheng.media.mapper.MediaProcessHistoryMapper;
import link.tomorinao.xuecheng.media.mapper.MediaProcessMapper;
import link.tomorinao.xuecheng.media.model.po.MediaFiles;
import link.tomorinao.xuecheng.media.model.po.MediaProcess;
import link.tomorinao.xuecheng.media.model.po.MediaProcessHistory;
import link.tomorinao.xuecheng.media.service.MediaProcessService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MediaProcessServiceImpl implements MediaProcessService {

    private final MediaProcessMapper mediaProcessMapper;
    private final MediaProcessHistoryMapper mediaProcessHistoryMapper;

    private final MediaFilesMapper mediaFilesMapper;

    public MediaProcessServiceImpl(MediaProcessMapper mediaProcessMapper, MediaProcessHistoryMapper mediaProcessHistoryMapper, MediaFilesMapper mediaFilesMapper) {
        this.mediaProcessMapper = mediaProcessMapper;
        this.mediaProcessHistoryMapper = mediaProcessHistoryMapper;
        this.mediaFilesMapper = mediaFilesMapper;
    }

    @Override
    public List<MediaProcess> selectList(Integer shardTotal, Integer shardIndex, Integer limit) {
        return mediaProcessMapper.selectList(shardTotal, shardIndex, limit);
    }

    @Override
    public Boolean startTask(Long id) {
        Integer started = mediaProcessMapper.startTask(id);
        return started > 0;
    }

    @Override
    @Transactional
    public void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg) {
        // 查任务
        MediaProcess mediaProcess = mediaProcessMapper.selectById(taskId);
        if (mediaProcess == null) {
            return;
        }
        // 如果失败
        if ("3".equals(status)) {
            mediaProcess.setStatus(status);
            mediaProcess.setErrormsg(errorMsg);
            mediaProcess.setFailCount(mediaProcess.getFailCount() + 1);
            mediaProcessMapper.updateById(mediaProcess);
            return;
        }
        // 任务成功，将其从待处理任务表中删除，同时新增历史处理表记录
        if ("2".equals(status)) {
            mediaProcess.setStatus(status);
            mediaProcess.setUrl(url);
            mediaProcess.setFinishDate(LocalDateTime.now());
            mediaProcessMapper.updateById(mediaProcess);
            MediaProcessHistory mediaProcessHistory = new MediaProcessHistory();
            // 两表仅有主键id不同
            BeanUtils.copyProperties(mediaProcess, mediaProcessHistory);
            mediaProcessHistory.setId(null); // 让它自增
            // 向历史处理表新增数据
            mediaProcessHistoryMapper.insert(mediaProcessHistory);
            // 同时删除待处理任务表中的数据
            mediaProcessMapper.deleteById(taskId);
//            // 更新媒资url
//            MediaFiles mediaFiles = new MediaFiles();
//            mediaFiles.setId(mediaProcess.getFileId());
//            mediaFiles.setUrl(url);
//            mediaFilesMapper.updateById(mediaFiles);
        }
    }
}
