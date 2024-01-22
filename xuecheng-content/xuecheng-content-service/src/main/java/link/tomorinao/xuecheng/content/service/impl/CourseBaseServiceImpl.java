package link.tomorinao.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import link.tomorinao.xuecheng.base.model.PageParams;
import link.tomorinao.xuecheng.base.model.PageResult;
import link.tomorinao.xuecheng.content.mapper.CourseBaseMapper;
import link.tomorinao.xuecheng.content.mapper.CourseCategoryMapper;
import link.tomorinao.xuecheng.content.mapper.CourseMarketMapper;
import link.tomorinao.xuecheng.content.model.dto.QueryCourseParamsDto;
import link.tomorinao.xuecheng.content.model.po.CourseBase;
import link.tomorinao.xuecheng.content.service.ICourseBaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 课程基本信息 服务实现类
 * </p>
 *
 * @author tomorinao
 * @since 2023-10-04
 */
@Slf4j
@Service
public class CourseBaseServiceImpl implements ICourseBaseService {

    @Resource
    private CourseBaseMapper courseBaseMapper;
    @Resource
    private CourseMarketMapper courseMarketMapper;
    @Resource
    private CourseCategoryMapper courseCategoryMapper;


    @Override
    public PageResult<CourseBase> list(Long companyId, PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {
        Long pageNo = pageParams.getPageNo();
        Long pageSize = pageParams.getPageSize();
        Page<CourseBase> page = new Page<>(pageNo, pageSize);

        LambdaQueryWrapper<CourseBase> wrapper = new LambdaQueryWrapper<>();
        String courseName = queryCourseParamsDto.getCourseName();
        String auditStatus = queryCourseParamsDto.getAuditStatus();
        String publishStatus = queryCourseParamsDto.getPublishStatus();
        wrapper.like(StringUtils.isNotEmpty(courseName), CourseBase::getName, courseName);
        wrapper.eq(StringUtils.isNotEmpty(auditStatus), CourseBase::getAuditStatus, auditStatus);
        wrapper.eq(StringUtils.isNotEmpty(publishStatus), CourseBase::getStatus, publishStatus);
        wrapper.eq(CourseBase::getCompanyId, companyId);

        Page<CourseBase> selectedPage = courseBaseMapper.selectPage(page, wrapper);
        List<CourseBase> records = selectedPage.getRecords();
        long total = selectedPage.getTotal();
        PageResult<CourseBase> pageResult = new PageResult<>(records, total, pageNo, pageSize);
        return pageResult;
    }

//    @Transactional
//    @Override
//    public CourseBaseInfoDto add(Long companyId, CourseInfoDto dto) {
//        // 1.参数校验
////        if (StringUtils.isBlank(dto.getName())) {
////            XueChengException.cast("课程名称不能为空");
////        }
////        if (StringUtils.isBlank(dto.getUsers())) {
////            XueChengException.cast("适用人群不能为空");
////        }
////        if (StringUtils.isBlank(dto.getMt())) {
////            XueChengException.cast("课程分类不能为空");
////        }
////        if (StringUtils.isBlank(dto.getSt())) {
////            XueChengException.cast("课程分类不能为空");
////        }
////        if (StringUtils.isBlank(dto.getGrade())) {
////            XueChengException.cast("课程等级不能为空");
////        }
////        if (StringUtils.isBlank(dto.getCharge())) {
////            XueChengException.cast("收费规则不能为空");
////        }
//        // 2.写 课程基本信息表
//        CourseBase courseBase = new CourseBase();
//        // 复制相同名称的属性。可能会用null覆盖有值的属性。
//        BeanUtils.copyProperties(dto,courseBase);
//        // AddCourseDto中没有的属性
//        courseBase.setCompanyId(companyId);
//        courseBase.setAuditStatus("202002"); // 审核状态默认 未提交
//        courseBase.setStatus("203001"); // 发布状态默认 未发布
//        int insert = courseBaseMapper.insert(courseBase);
//        if(insert<=0){
//            XueChengException.cast("添加课程失败");
//        }
//        //3.写 课程营销表
//        CourseMarket market = new CourseMarket();
//        BeanUtils.copyProperties(dto,market);
//        // 通过主键相同实现一一对应
//        Long id = courseBase.getId();
//        market.setId(id);
//        this.saveCourseMarket(market);
//        CourseBaseInfoDto courseBaseInfoDto = getById(id);
//        return courseBaseInfoDto;
//    }
//
//    @Override
//    public CourseBaseInfoDto getById(Long id) {
//        CourseBase po = courseBaseMapper.selectById(id);
//        CourseBaseInfoDto dto = new CourseBaseInfoDto();
//        BeanUtils.copyProperties(po,dto);
//        // 补充mt, st具体名称，返回dto
//        CourseCategory mt_category = courseCategoryMapper.selectById(po.getMt());
//        dto.setMtName(mt_category.getName());
//        CourseCategory st_category = courseCategoryMapper.selectById(po.getSt());
//        dto.setMtName(st_category.getName());
//        return dto;
//    }
//
//    private int saveCourseMarket(CourseMarket market) {
//        // 校验收费规则
//        String charge = market.getCharge();
//        if (charge.equals("201001")) { //如果收费，检查价格
//            Double price = market.getPrice();
//            if (price == null || price < 0){
//                XueChengException.cast("价格需要是正数");
//            }
//        }
//        Long id = market.getId();
//        CourseMarket db_market = courseMarketMapper.selectById(id);
//        if(db_market==null){
//            return courseMarketMapper.insert(market);
//        }else {
//            return courseMarketMapper.updateById(market);
//        }
//    }
}
