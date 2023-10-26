package link.tomorinao.xuecheng.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import link.tomorinao.xuecheng.content.model.dto.TeachplanDto;
import link.tomorinao.xuecheng.content.model.po.Teachplan;

import java.util.List;

/**
 * <p>
 * 课程计划 服务类
 * </p>
 *
 * @author tomorinao
 * @since 2023-10-04
 */
public interface ITeachplanService{

    List<TeachplanDto> getTreeNodes(Long id);

    void addOrUpdateById(TeachplanDto dto);

    void deleteById(Long id);

    void moveup(Long id);

    void movedown(Long id);
}
