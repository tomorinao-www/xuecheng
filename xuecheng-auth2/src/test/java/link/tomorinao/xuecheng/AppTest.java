package link.tomorinao.xuecheng;


import link.tomorinao.xuecheng.auth.mapper.XcUserMapper;
import link.tomorinao.xuecheng.auth.model.po.XcUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

@SpringBootTest
public class AppTest {
    @Autowired
    XcUserMapper xcUserMapper;

    @Test
    void testBCrypt() {
        XcUser xcUser = new XcUser();
        xcUser.setUsername("tomorinao");
        xcUser.setPassword(new BCryptPasswordEncoder().encode("123456"));
        xcUser.setName("测试学生");
        xcUser.setUtype("101001");
        xcUser.setStatus("1");
        xcUser.setCreateTime(LocalDateTime.now());
        xcUserMapper.insert(xcUser);
    }
}
