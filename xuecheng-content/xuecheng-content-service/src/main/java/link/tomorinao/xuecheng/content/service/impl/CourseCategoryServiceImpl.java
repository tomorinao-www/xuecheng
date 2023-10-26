package link.tomorinao.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import link.tomorinao.xuecheng.content.mapper.CourseCategoryMapper;
import link.tomorinao.xuecheng.content.model.dto.CourseCategoryTreeDto;
import link.tomorinao.xuecheng.content.model.po.CourseCategory;
import link.tomorinao.xuecheng.content.service.ICourseCategoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 课程分类 服务实现类
 * </p>
 *
 * @author tomorinao
 * @since 2023-10-04
 */
@Service
public class CourseCategoryServiceImpl extends ServiceImpl<CourseCategoryMapper, CourseCategory> implements ICourseCategoryService {
    @Resource
    CourseCategoryMapper courseCategoryMapper;

    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
        List<CourseCategoryTreeDto> nodes = courseCategoryMapper.selectTreeNodes(id);
        Map<String, CourseCategoryTreeDto> map = nodes.stream().filter(x -> !x.getId().equals(id)).collect(Collectors.toMap(CourseCategory::getId, x -> x, (k1, k2) -> k2));
        List<CourseCategoryTreeDto> result = new ArrayList<>();
        nodes.stream().filter(x -> !x.getId().equals(id)).forEach(node -> {
                    String parentid = node.getParentid();
                    // 是根节点，就加入结果列表
                    if (parentid.equals(id)) {
                        result.add(node);
                        return;
                    }

                    CourseCategoryTreeDto p_node = map.get(parentid);
                    // 父节点不存在
                    if (p_node == null) {
                        return;
                    }
                    // 不是根节点，就作为父节点的儿子
                    p_node.addChild(node);
                }
        );
        return result;
    }
}
