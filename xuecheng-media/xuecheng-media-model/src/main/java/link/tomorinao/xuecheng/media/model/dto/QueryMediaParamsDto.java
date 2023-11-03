package link.tomorinao.xuecheng.media.model.dto;

import lombok.Data;
import lombok.ToString;

/**
 * @author Mr.M
 * @version 1.0
 * @description 媒资文件查询请求模型类
 * @date 2022/9/10 8:53
 */
@Data
@ToString
public class QueryMediaParamsDto {

    private String filename;
    private String fileType;
    private String auditStatus;
}
