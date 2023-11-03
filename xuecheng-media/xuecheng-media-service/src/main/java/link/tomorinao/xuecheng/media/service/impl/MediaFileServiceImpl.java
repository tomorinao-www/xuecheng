package link.tomorinao.xuecheng.media.service.impl;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import link.tomorinao.xuecheng.base.model.PageParams;
import link.tomorinao.xuecheng.base.model.PageResult;
import link.tomorinao.xuecheng.base.utils.BeanUtils;
import link.tomorinao.xuecheng.media.mapper.MediaFilesMapper;
import link.tomorinao.xuecheng.media.minio.MinioUtil;
import link.tomorinao.xuecheng.media.model.dto.QueryMediaParamsDto;
import link.tomorinao.xuecheng.media.model.dto.UploadFileDto;
import link.tomorinao.xuecheng.media.model.po.MediaFiles;
import link.tomorinao.xuecheng.media.service.MediaFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2022/9/10 8:58
 */
@Service
public class MediaFileServiceImpl implements MediaFileService {

    @Autowired
    MediaFilesMapper mediaFilesMapper;

    @Resource
    MinioUtil minioUtil;

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

    private UploadFileDto po2dto(MediaFiles po) {
        UploadFileDto dto = new UploadFileDto();
        BeanUtils.copyPropertiesIgnoreNull(po, dto);
        return dto;
    }

}
