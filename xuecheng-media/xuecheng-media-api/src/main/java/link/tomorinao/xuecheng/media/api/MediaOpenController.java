package link.tomorinao.xuecheng.media.api;

import link.tomorinao.xuecheng.base.exception.XueChengException;
import link.tomorinao.xuecheng.base.model.RestResponse;
import link.tomorinao.xuecheng.media.mapper.MediaFilesMapper;
import link.tomorinao.xuecheng.media.model.po.MediaFiles;
import link.tomorinao.xuecheng.media.service.MediaFileService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/open")
public class MediaOpenController {
    @Autowired
    private MediaFilesMapper MediaFilesMapper;

    @GetMapping("/preview/{mediaId}")
    public RestResponse<String> getMediaUrl(@PathVariable String mediaId) {
        MediaFiles mediaFile = MediaFilesMapper.selectById(mediaId);
        if (mediaFile == null || StringUtils.isEmpty(mediaFile.getUrl())) {
            XueChengException.cast("视频还没有转码处理");
        }
        return RestResponse.success(mediaFile.getUrl());
    }
}