package link.tomorinao.xuecheng.media.api;

import link.tomorinao.xuecheng.base.model.PageParams;
import link.tomorinao.xuecheng.base.model.PageResult;
import link.tomorinao.xuecheng.base.model.RestResponse;
import link.tomorinao.xuecheng.media.model.dto.QueryMediaParamsDto;
import link.tomorinao.xuecheng.media.model.dto.UploadFileDto;
import link.tomorinao.xuecheng.media.model.po.MediaFiles;
import link.tomorinao.xuecheng.media.service.MediaFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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


    private final MediaFileService mediaFileService;

    @Autowired
    public MediaFilesController(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }


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


    @PostMapping("/upload/checkfile")
    public RestResponse<Boolean> checkFile(String fileMd5) {
        return mediaFileService.checkFile(fileMd5);
    }

    @PostMapping("/upload/checkchunk")
    public RestResponse<Boolean> checkChunk(String fileMd5,
                                            int chunk) {
        return mediaFileService.checkChunk(fileMd5, chunk);
    }

    @PostMapping("/upload/uploadchunk")
    public RestResponse<Boolean> uploadChunk(MultipartFile file,
                                             String fileMd5,
                                             int chunk) {
        return mediaFileService.uploadChunk(fileMd5, chunk, file);
    }

    @PostMapping("/upload/mergechunks")
    public RestResponse<Boolean> mergeChunks(String fileMd5,
                                    String fileName,
                                    int chunkTotal,
                                    String etag){
        Long companyId = 1232141425L;
        return mediaFileService.mergeChunks(companyId, fileMd5, fileName, chunkTotal, etag);
    }
}
