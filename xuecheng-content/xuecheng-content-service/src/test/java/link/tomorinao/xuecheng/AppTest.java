package link.tomorinao.xuecheng;


import jakarta.annotation.Resource;
import link.tomorinao.xuecheng.content.mapper.TeachplanMapper;
import link.tomorinao.xuecheng.content.model.dto.TeachplanDto;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class AppTest {

    @Resource
    private TeachplanMapper teachplanMapper;
    @Test
    public void test() {
        List<TeachplanDto> teachplanDtos = teachplanMapper.selectTreeNodes(117L);
        System.out.println(teachplanDtos);
    }
}
