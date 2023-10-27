package link.tomorinao.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import link.tomorinao.xuecheng.base.exception.XueChengException;
import link.tomorinao.xuecheng.content.mapper.CourseCategoryMapper;
import link.tomorinao.xuecheng.content.mapper.CourseInfoMapper;
import link.tomorinao.xuecheng.content.mapper.CourseTeacherMapper;
import link.tomorinao.xuecheng.content.mapper.TeachplanMapper;
import link.tomorinao.xuecheng.content.model.dto.CourseInfoDto;
import link.tomorinao.xuecheng.content.model.po.CourseCategory;
import link.tomorinao.xuecheng.content.model.po.CourseInfo;
import link.tomorinao.xuecheng.content.model.po.CourseTeacher;
import link.tomorinao.xuecheng.content.model.po.Teachplan;
import link.tomorinao.xuecheng.content.service.ICourseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import link.tomorinao.xuecheng.base.utils.BeanUtils;

@Slf4j
@Service
public class CourseInfoServiceImpl implements ICourseInfoService {

    @Resource
    private CourseInfoMapper courseInfoMapper;
    @Resource
    private CourseCategoryMapper courseCategoryMapper;

    @Resource
    private CourseTeacherMapper courseTeacherMapper;

    @Resource
    private TeachplanMapper teachplanMapper;

    @Override
    public CourseInfoDto getById(Long id) {
        CourseInfo po = courseInfoMapper.selectById(id);
        return po2dto(po);
    }


    @Transactional
    @Override
    public Long add(Long companyId, CourseInfoDto dto) {
        // 1.参数校验
        valid(dto);
        // 2.dto转po
        CourseInfo po = dto2po(companyId, dto);
        // 3.写数据库
        int i = courseInfoMapper.insert(po);
        if (i <= 0) {
            XueChengException.cast("添加课程失败");
        }
        return po.getId();
    }


    @Override
    public Long updateById(Long companyId, CourseInfoDto dto) {
        // 1.鉴权 - 只能修改本机构的课程
        CourseInfo po = courseInfoMapper.selectById(dto.getId());
        authenticate(companyId, po);
        // 2.参数校验
        valid(dto);
        // 3.dto转po
        BeanUtils.copyPropertiesIgnoreNull(dto, po);
        // 4.写数据库
        int i = courseInfoMapper.updateById(po);
        if (i <= 0) {
            XueChengException.cast("修改课程失败");
        }
        return po.getId();
    }

    @Transactional
    @Override
    public void deleteById(Long companyId, Long id) {
        // 鉴权
        CourseInfo courseInfoPO = courseInfoMapper.selectById(id);
        authenticate(companyId, courseInfoPO);
        // 删除章节
        LambdaQueryWrapper<Teachplan> planWrapper = new LambdaQueryWrapper<>();
        planWrapper.eq(Teachplan::getCourseId, id);
        teachplanMapper.delete(planWrapper);
        // 删除教师
        LambdaQueryWrapper<CourseTeacher> teacherWrapper = new LambdaQueryWrapper<>();
        teacherWrapper.eq(CourseTeacher::getCourseId, id);
        courseTeacherMapper.delete(teacherWrapper);
        // 删除课程信息
        courseInfoMapper.deleteById(id);
    }

    private void authenticate(Long companyId, CourseInfo po) {
        if (!companyId.equals(po.getCompanyId())) {
            XueChengException.cast("只能操作本机构的课程");
        }
    }

    private CourseInfoDto po2dto(CourseInfo po) {
        CourseInfoDto dto = new CourseInfoDto();
        BeanUtils.copyPropertiesIgnoreNull(po, dto);
        // 补充mt, st具体名称
        CourseCategory mt_category = courseCategoryMapper.selectById(po.getMt());
        dto.setMtName(mt_category.getName());
        CourseCategory st_category = courseCategoryMapper.selectById(po.getSt());
        dto.setStName(st_category.getName());
        return dto;
    }


    private CourseInfo dto2po(Long companyId, CourseInfoDto dto) {
        CourseInfo po = new CourseInfo();
        // 复制相同名称的属性。忽略null
        BeanUtils.copyPropertiesIgnoreNull(dto, po);
        // 设置dto中没有的companyId
        po.setCompanyId(companyId);
        return po;
    }

    /*
    课程信息参数合法性检验
     */
    private void valid(CourseInfoDto dto) {
        String charge = dto.getCharge();
        if (charge.equals("201001")) { //如果收费，检查价格
            Double price = dto.getPrice();
            if (price == null || price < 0) {
                XueChengException.cast("价格不能是负数");
            }
        }
    }
}
