package link.tomorinao.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import link.tomorinao.xuecheng.base.exception.XueChengException;
import link.tomorinao.xuecheng.base.utils.BeanUtils;
import link.tomorinao.xuecheng.content.mapper.TeachplanMapper;
import link.tomorinao.xuecheng.content.model.dto.TeachplanDto;
import link.tomorinao.xuecheng.content.model.po.CourseCategory;
import link.tomorinao.xuecheng.content.model.po.Teachplan;
import link.tomorinao.xuecheng.content.service.ITeachplanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 课程计划 服务实现类
 * </p>
 *
 * @author tomorinao
 * @since 2023-10-04
 */
@Slf4j
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

    @Transactional
    @Override
    public void deleteById(Long id) {
        Teachplan po = teachplanMapper.selectById(id);
        Long parentid = po.getParentid();
        // 一级章节，需要同时删除二级小节
        LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teachplan::getParentid, id);
        int deletei = teachplanMapper.delete(wrapper);
        log.info("删除返回deletei->{}", deletei);
        teachplanMapper.deleteById(id);
    }

    @Transactional
    @Override
    public void moveup(Long id) {
        Teachplan po = teachplanMapper.selectById(id);
        Teachplan uppo = teachplanMapper.selectUpOne(po);
        if (uppo == null) {
            return;
        }
        int order = po.getOrderby();
        int upOrder = uppo.getOrderby();
        po.setOrderby(upOrder);
        uppo.setOrderby(order);
        teachplanMapper.updateById(po);
        teachplanMapper.updateById(uppo);
    }

    @Override
    public void movedown(Long id) {
        Teachplan po = teachplanMapper.selectById(id);
        Teachplan downpo = teachplanMapper.selectDownOne(po);
        if (downpo == null) {
            return;
        }
        int order = po.getOrderby();
        int upOrder = downpo.getOrderby();
        po.setOrderby(upOrder);
        downpo.setOrderby(order);
        teachplanMapper.updateById(po);
        teachplanMapper.updateById(downpo);
    }

    private Teachplan dto2po(TeachplanDto dto) {
        Teachplan po = new Teachplan();
        BeanUtils.copyPropertiesIgnoreNull(dto, po);
        Integer orderby = teachplanMapper.selectMaxOrderby(po);
        po.setOrderby(orderby == null ? 1 : orderby + 1);
        return po;
    }

}
