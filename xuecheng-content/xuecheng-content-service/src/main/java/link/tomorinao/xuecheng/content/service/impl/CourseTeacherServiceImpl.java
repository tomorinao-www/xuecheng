package link.tomorinao.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import link.tomorinao.xuecheng.content.mapper.CourseTeacherMapper;
import link.tomorinao.xuecheng.content.model.po.CourseTeacher;
import link.tomorinao.xuecheng.content.service.ICourseTeacherService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 课程-教师关系表 服务实现类
 * </p>
 *
 * @author tomorinao
 * @since 2023-10-04
 */
@Service
public class CourseTeacherServiceImpl extends ServiceImpl<CourseTeacherMapper, CourseTeacher> implements ICourseTeacherService {

    @Resource
    private CourseTeacherMapper courseTeacherMapper;

    @Override
    public List<CourseTeacher> getBycourseId(Long courseId) {
        LambdaQueryWrapper<CourseTeacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseTeacher::getCourseId, courseId);
        List<CourseTeacher> teacherList = courseTeacherMapper.selectList(wrapper);
        return teacherList;
    }
}
