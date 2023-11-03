package link.tomorinao.xuecheng.media.api;

import link.tomorinao.xuecheng.base.model.PageParams;
import link.tomorinao.xuecheng.base.model.PageResult;
import link.tomorinao.xuecheng.media.model.dto.QueryMediaParamsDto;
import link.tomorinao.xuecheng.media.model.dto.UploadFileDto;
import link.tomorinao.xuecheng.media.model.po.MediaFiles;
import link.tomorinao.xuecheng.media.service.MediaFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Mr.M
 * @version 1.0
 * @description 媒资文件管理接口
 * @date 2022/9/6 11:29
 */
@RestController
public class MediaFilesController {


    @Autowired
    MediaFileService mediaFileService;


    @PostMapping("/files")
    public PageResult<MediaFiles> list(PageParams pageParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto) {
        Long companyId = 1232141425L;
        return mediaFileService.queryMediaFiels(companyId, pageParams, queryMediaParamsDto);

    }

    @PostMapping("/upload/coursefile")
    public UploadFileDto uploadCourseFile(MultipartFile filedata) throws Exception {
        Long companyId = 1232141425L;
        return mediaFileService.uploadCourseFile(companyId, filedata);
    }

}
