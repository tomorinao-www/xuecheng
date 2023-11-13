package link.tomorinao.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import link.tomorinao.xuecheng.base.exception.XueChengException;
import link.tomorinao.xuecheng.base.utils.BeanUtils;
import link.tomorinao.xuecheng.content.mapper.TeachplanMapper;
import link.tomorinao.xuecheng.content.mapper.TeachplanMediaMapper;
import link.tomorinao.xuecheng.content.model.dto.BindTeachplanMediaDto;
import link.tomorinao.xuecheng.content.model.dto.TeachplanDto;
import link.tomorinao.xuecheng.content.model.po.Teachplan;
import link.tomorinao.xuecheng.content.model.po.TeachplanMedia;
import link.tomorinao.xuecheng.content.service.ITeachplanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    private TeachplanMapper teachplanMapper;
    private TeachplanMediaMapper teachplanMediaMapper;

    @Autowired
    public TeachplanServiceImpl(TeachplanMapper teachplanMapper, TeachplanMediaMapper teachplanMediaMapper) {
        this.teachplanMapper = teachplanMapper;
        this.teachplanMediaMapper = teachplanMediaMapper;
    }


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
        if (parentid == 0) {
            LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Teachplan::getParentid, id);
            teachplanMapper.delete(wrapper);
        }
        teachplanMapper.deleteById(id);
    }

    @Transactional
    @Override
    public void moveup(Long id) {
        Teachplan po = teachplanMapper.selectById(id);
        Teachplan uppo = teachplanMapper.selectUpOne(po);
        exchangeOrderby(uppo, po);
    }

    @Override
    public void movedown(Long id) {
        Teachplan po = teachplanMapper.selectById(id);
        Teachplan downpo = teachplanMapper.selectDownOne(po);
        exchangeOrderby(downpo, po);
    }

    @Override
    public void associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto) {
        Long teachplanId = bindTeachplanMediaDto.getTeachplanId();
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if(teachplan==null){
            XueChengException.cast("课程章节id不存在！");
        }
        if(2!=teachplan.getGrade()){
            XueChengException.cast("只允许二级小节绑定媒资！");
        }
        // 增加或更新
        TeachplanMedia teachplanMedia = new TeachplanMedia();
        teachplanMedia.setTeachplanId(bindTeachplanMediaDto.getTeachplanId());
        teachplanMedia.setMediaFilename(bindTeachplanMediaDto.getFileName());
        teachplanMedia.setMediaId(bindTeachplanMediaDto.getMediaId());
        teachplanMedia.setCourseId(teachplan.getCourseId());
        LambdaQueryWrapper<TeachplanMedia> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeachplanMedia::getTeachplanId, teachplanId);
        TeachplanMedia teachplanMedia_old = teachplanMediaMapper.selectOne(wrapper);
        if(teachplanMedia_old==null){
            teachplanMediaMapper.insert(teachplanMedia);
        }else {
            teachplanMediaMapper.update(teachplanMedia,wrapper);
        }
    }

    private void exchangeOrderby(Teachplan po1, Teachplan po2) {
        if (po1 == null || po2 == null) {
            return;
        }
        int order1 = po1.getOrderby();
        int order2 = po2.getOrderby();
        po2.setOrderby(order1);
        po1.setOrderby(order2);
        teachplanMapper.updateById(po2);
        teachplanMapper.updateById(po1);
    }

    private Teachplan dto2po(TeachplanDto dto) {
        Teachplan po = new Teachplan();
        BeanUtils.copyPropertiesIgnoreNull(dto, po);
        Integer orderby = teachplanMapper.selectMaxOrderby(po);
        po.setOrderby(orderby == null ? 1 : orderby + 1);
        return po;
    }

}
