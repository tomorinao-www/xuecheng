package link.tomorinao.xuecheng.content.mapper;

import link.tomorinao.xuecheng.content.model.dto.TeachplanDto;
import link.tomorinao.xuecheng.content.model.po.Teachplan;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 课程计划 Mapper 接口
 * </p>
 *
 * @author tomorinao
 * @since 2023-10-04
 */
@Mapper
public interface TeachplanMapper extends BaseMapper<Teachplan> {

    List<TeachplanDto> selectTreeNodes(Long id);

    Integer selectMaxOrderby(Teachplan teachplan);

    Teachplan selectUpOne(Teachplan teachplan);

    Teachplan selectDownOne(Teachplan po);
}
