package link.tomorinao.xuecheng.content.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import link.tomorinao.xuecheng.base.exception.XueChengException;
import link.tomorinao.xuecheng.base.utils.BeanUtils;
import link.tomorinao.xuecheng.content.mapper.CourseAuditMapper;
import link.tomorinao.xuecheng.content.mapper.CourseCategoryMapper;
import link.tomorinao.xuecheng.content.mapper.CourseInfoMapper;
import link.tomorinao.xuecheng.content.mapper.CoursePublishPreMapper;
import link.tomorinao.xuecheng.content.model.dto.TeachplanDto;
import link.tomorinao.xuecheng.content.model.po.*;
import link.tomorinao.xuecheng.content.service.ICourseAuditService;
import link.tomorinao.xuecheng.content.service.ITeachplanService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tomorinao
 * @since 2023-10-04
 */
@Service
public class CourseAuditServiceImpl extends ServiceImpl<CourseAuditMapper, CourseAudit> implements ICourseAuditService {

    private CourseInfoMapper courseInfoMapper;
    private ITeachplanService iTeachplanService;
    private CoursePublishPreMapper coursePublishPreMapper;
    private CourseCategoryMapper courseCategoryMapper;

    @Autowired
    public CourseAuditServiceImpl(CourseInfoMapper courseInfoMapper, ITeachplanService iTeachplanService, CoursePublishPreMapper coursePublishPreMapper, CourseCategoryMapper courseCategoryMapper) {
        this.courseInfoMapper = courseInfoMapper;
        this.iTeachplanService = iTeachplanService;
        this.coursePublishPreMapper = coursePublishPreMapper;
        this.courseCategoryMapper = courseCategoryMapper;
    }

    @Transactional
    @Override
    public void commitAudit(Long companyId, Long courseId) {
        // 查询课程信息
        CourseInfo courseInfo = courseInfoMapper.selectById(courseId);
        // 查询课程计划
        List<TeachplanDto> teachplanTree = iTeachplanService.getTreeNodes(courseId);

        // 1. 约束
        String auditStatus = courseInfo.getAuditStatus();
        // 1.1 审核完后，方可提交审核
        if ("202003".equals(auditStatus)) {
            XueChengException.cast("该课程现在属于待审核状态，审核完成后可再次提交");
        }
        // 1.2 本机构只允许提交本机构的课程
        if (!companyId.equals(courseInfo.getCompanyId())) {
            XueChengException.cast("本机构只允许提交本机构的课程");
        }
        // 1.3 没有上传图片，不允许提交
        if (StringUtils.isEmpty(courseInfo.getPic())) {
            XueChengException.cast("没有上传课程封面，不允许提交审核");
        }
        // 1.4 没有添加课程计划，不允许提交审核
        if (teachplanTree.isEmpty()) {
            XueChengException.cast("没有添加课程计划，不允许提交审核");
        }
        // 2. 封装课程发布对象
        CoursePublishPre coursePublishPre = new CoursePublishPre();
        BeanUtils.copyProperties(courseInfo, coursePublishPre);
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(courseInfo, courseMarket);
        coursePublishPre.setMarket(JSON.toJSONString(courseMarket));
        coursePublishPre.setTeachplan(JSON.toJSONString(teachplanTree));
        coursePublishPre.setCompanyId(companyId);

        // 补充mt, st具体名称
        CourseCategory mt_category = courseCategoryMapper.selectById(coursePublishPre.getMt());
        coursePublishPre.setMtName(mt_category.getName());
        CourseCategory st_category = courseCategoryMapper.selectById(coursePublishPre.getSt());
        coursePublishPre.setStName(st_category.getName());
        // 3. 设置预发布记录状态为已提交
        coursePublishPre.setStatus("202003");
        // 判断是否已经存在预发布记录，若存在，则更新
        CoursePublishPre coursePublishPreUpdate = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPreUpdate == null) {
            coursePublishPreMapper.insert(coursePublishPre);
        } else {
            coursePublishPreMapper.updateById(coursePublishPre);
        }
        // 4. 设置课程基本信息审核状态为已提交
        courseInfo.setAuditStatus("202003");
        courseInfoMapper.updateById(courseInfo);
    }
}
