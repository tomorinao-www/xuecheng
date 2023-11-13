package link.tomorinao.xuecheng.media.service;

import link.tomorinao.xuecheng.media.model.po.MediaProcess;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MediaProcessService {

    List<MediaProcess> selectList(Integer shardTotal,
                                  Integer shardIndex,
                                  Integer limit);

    Boolean startTask(Long id);

    void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg);
}
