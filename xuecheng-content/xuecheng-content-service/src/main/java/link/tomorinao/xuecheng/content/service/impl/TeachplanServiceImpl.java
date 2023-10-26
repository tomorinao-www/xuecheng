package link.tomorinao.xuecheng.content.service.impl;

import jakarta.annotation.Resource;
import link.tomorinao.xuecheng.base.utils.BeanUtils;
import link.tomorinao.xuecheng.content.mapper.TeachplanMapper;
import link.tomorinao.xuecheng.content.model.dto.TeachplanDto;
import link.tomorinao.xuecheng.content.model.po.Teachplan;
import link.tomorinao.xuecheng.content.service.ITeachplanService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 课程计划 服务实现类
 * </p>
 *
 * @author tomorinao
 * @since 2023-10-04
 */
@Service
public class TeachplanServiceImpl implements ITeachplanService {

    @Resource
    private TeachplanMapper teachplanMapper;

    @Override
    public List<TeachplanDto> getTreeNodes(Long id) {
        List<TeachplanDto> treeNodes = teachplanMapper.selectTreeNodes(id);
        return treeNodes;
    }

    @Override
    public void addOrUpdateById(TeachplanDto dto) {
        Long id = dto.getId();
        if (id == null) {
            Teachplan po = dto2po(dto);
            BeanUtils.copyPropertiesIgnoreNull(dto, po);
            teachplanMapper.insert(po);
        } else {
            Teachplan po = teachplanMapper.selectById(id);
            BeanUtils.copyPropertiesIgnoreNull(dto, po);
            teachplanMapper.updateById(po);
        }
    }

    @Override
    public void deleteById(Long id) {
        teachplanMapper.deleteById(id);
    }

    private Teachplan dto2po(TeachplanDto dto) {
        Teachplan po = new Teachplan();
        BeanUtils.copyPropertiesIgnoreNull(dto, po);
        Integer orderby = teachplanMapper.selectMaxOrderby(po);
        po.setOrderby(orderby + 1);
        return po;
    }

}
