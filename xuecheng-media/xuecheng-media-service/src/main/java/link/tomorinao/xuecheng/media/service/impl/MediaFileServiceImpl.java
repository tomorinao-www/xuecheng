package link.tomorinao.xuecheng.media.service.impl;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.minio.Result;
import io.minio.StatObjectResponse;
import io.minio.messages.DeleteError;
import link.tomorinao.xuecheng.base.model.PageParams;
import link.tomorinao.xuecheng.base.model.PageResult;
import link.tomorinao.xuecheng.base.model.RestResponse;
import link.tomorinao.xuecheng.base.utils.BeanUtils;
import link.tomorinao.xuecheng.media.mapper.MediaFilesMapper;
import link.tomorinao.xuecheng.media.minio.MinioUtil;
import link.tomorinao.xuecheng.media.model.dto.QueryMediaParamsDto;
import link.tomorinao.xuecheng.media.model.dto.UploadFileDto;
import link.tomorinao.xuecheng.media.model.po.MediaFiles;
import link.tomorinao.xuecheng.media.service.MediaFileService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2022/9/10 8:58
 */
@Service
@Slf4j
public class MediaFileServiceImpl implements MediaFileService {

    private final MediaFilesMapper mediaFilesMapper;

    private final MinioUtil minioUtil;
    final private int chunkSize = 1024 * 1024 * 5;

    @Autowired
    public MediaFileServiceImpl(MediaFilesMapper mediaFilesMapper, MinioUtil minioUtil) {
        this.mediaFilesMapper = mediaFilesMapper;
        this.minioUtil = minioUtil;
    }

    @Override
    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {
        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();
        //分页对象
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        PageResult<MediaFiles> mediaListResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
        return mediaListResult;
    }

    @Override
    public UploadFileDto uploadCourseFile(Long companyId, MultipartFile multipartFile) throws Exception {
        // 计算md5
        byte[] bytes = multipartFile.getBytes();
        String md5 = DigestUtil.md5Hex(bytes);
        // 查库去重
        LambdaQueryWrapper<MediaFiles> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MediaFiles::getFileId, md5);
        MediaFiles po = mediaFilesMapper.selectOne(wrapper);
        if (po == null) {
            po = new MediaFiles();
            // 上传文件
            String filename = multipartFile.getOriginalFilename();
            String extName = FileNameUtil.extName(filename);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            String folder = dateFormat.format(new Date());
            String objectName = folder + "/" + md5 + "." + extName;
            String bucket = minioUtil.bucket.getMedia();
            String url = minioUtil.upload(multipartFile, objectName, bucket);
            // 构造po
            po.setId(md5);
            po.setCompanyId(companyId);
            po.setFilename(filename);
            po.setFileType("001001");
            po.setBucket(bucket);
            po.setFilePath(objectName);
            po.setFileId(md5);
            po.setUrl("/" + bucket + "/" + objectName);
            po.setStatus("1");
            po.setAuditStatus("002003");
            po.setFileSize(multipartFile.getSize());
            // 写数据库
            mediaFilesMapper.insert(po);
        }
        return po2dto(po);
    }

    @Override
    public RestResponse<Boolean> checkFile(String fileMd5) {
        // 1. 查数据库
        MediaFiles po = mediaFilesMapper.selectById(fileMd5);
        if (po == null) {
            return RestResponse.success(false);
        }
        // 2. 查minio
        try {
            StatObjectResponse stat = minioUtil.statObject(po.getFilePath(), po.getBucket());
        } catch (Exception e) {
            return RestResponse.success(false);
        }
        return RestResponse.success(true);
    }

    @Override
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex) {
        String objectPath = getChunkPath(fileMd5, chunkIndex);
        try {
            StatObjectResponse stat = minioUtil.statObject(objectPath, minioUtil.bucket.getVideo());
            // 检查块大小，如果小于5MB，删除已经上传的块
            if (stat.size() < chunkSize) {
                minioUtil.remove(objectPath, minioUtil.bucket.getVideo());
                return RestResponse.success(false);
            }
        } catch (Exception e) {
            return RestResponse.success(false);
        }
        return RestResponse.success(true);
    }

    @Override
    public RestResponse<Boolean> uploadChunk(String fileMd5, int chunkIndex, MultipartFile file) {
        try {
            minioUtil.upload(file, getChunkPath(fileMd5, chunkIndex), minioUtil.bucket.getVideo());
        } catch (Exception e) {
            return RestResponse.success(false);
        }
        return RestResponse.success(true);
    }

    @Override
    public RestResponse<Boolean> mergeChunks(Long companyId, String fileMd5, String fileName, int chunkTotal, String etag) {
        // 1.minio合并分块
        /*
        stream流的
        toList() 返回的List是Arrays的静态内部类，不可变List，不能增删改，但速度快
        collect.(Collectors.tolist()) 返回的List是正常ArrayList
         */
        List<String> chunkPathList = Stream.iterate(0, i -> i + 1).limit(chunkTotal)
                .map(i -> getChunkPath(fileMd5, i)).toList();
        String ext = FileNameUtil.extName(fileName);
        String objectPath = getFilePath(fileMd5, ext);
        String bucket = minioUtil.bucket.getVideo();
        try {
            minioUtil.compose(chunkPathList, objectPath, bucket);
        } catch (Exception e) {
            log.error("合并分块出错 {}", e.getMessage());
            return RestResponse.validfail("合并分块出错");
        }

        // 2.删除分块文件
        try {
            Iterable<Result<DeleteError>> results = minioUtil.remove(chunkPathList, bucket);
            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
                log.error("删除分块出错 {}; {}", error.objectName(), error.message());
            }
        } catch (Exception e) {
            log.error("删除分块出错", e);
            return RestResponse.validfail("删除分块出错");
        }

        // 3.校验etag
        try {
            StatObjectResponse stat = minioUtil.statObject(objectPath, bucket);
            if (!Objects.equals(etag, stat.etag())) {
                minioUtil.remove(objectPath, bucket);
                return RestResponse.validfail("校验文件etag失败！");
            }
        } catch (Exception e) {
            log.error("校验文件etag失败！", e);
            return RestResponse.validfail("校验文件etag失败！");
        }
//        try (InputStream inputStream = minioUtil.getObject(objectPath, bucket)) {
//            String objectMd5 = DigestUtil.md5Hex(inputStream);
//            if (!Objects.equals(fileMd5, objectMd5)) {
//                return RestResponse.validfail("校验文件md5失败！");
//            }
//        } catch (Exception e) {
//            log.error("校验文件md5失败！", e);
//            return RestResponse.validfail("校验文件md5失败！");
//        }

        // 4.写数据库
        MediaFiles po = new MediaFiles();
        long size;
        try {
            StatObjectResponse stat = minioUtil.statObject(objectPath, bucket);
            size = stat.size();
        } catch (Exception e) {
            log.error("获取对象信息出错", e);
            return RestResponse.validfail("获取对象信息出错");
        }
        po.setId(fileMd5);
        po.setCompanyId(companyId);
        po.setFilename(fileName);
        po.setFileType("001001");
        po.setBucket(bucket);
        po.setFilePath(objectPath);
        po.setFileId(etag);
        po.setUrl("/" + bucket + "/" + objectPath);
        po.setStatus("1");
        po.setAuditStatus("002003");
        po.setFileSize(size);
        mediaFilesMapper.insert(po);
        return RestResponse.success(true);
    }

    @NotNull
    private static String getParentPath(String md5) {
        return md5.charAt(0) + "/" + md5.charAt(1) + "/" + md5;
    }

    @NotNull
    private static String getFilePath(String md5, String ext) {
        return getParentPath(md5) + "/" + md5 + "." + ext;
    }

    @NotNull
    private static String getChunkPath(String md5, int chunkIndex) {
        return getParentPath(md5) + "/chunk/" + chunkIndex;
    }

    private UploadFileDto po2dto(MediaFiles po) {
        UploadFileDto dto = new UploadFileDto();
        BeanUtils.copyPropertiesIgnoreNull(po, dto);
        return dto;
    }

}
