package link.tomorinao.xuecheng.media.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import link.tomorinao.xuecheng.media.model.po.MediaProcess;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author itcast
 */
@Mapper
public interface MediaProcessMapper extends BaseMapper<MediaProcess> {

    @Select("select * from media_process where id % #{shardTotal} = #{shardIndex} and (status in (1,3)) and fail_count < 3 limit #{limit}")
    List<MediaProcess> selectList(@Param("shardTotal") Integer shardTotal,
                                  @Param("shardIndex") Integer shardIndex,
                                  @Param("limit") Integer limit);

    @Update("update media_process set status='4' where id = #{id} and status in (1,3) and fail_count<3")
    Integer startTask(@Param("id") Long id);
}
