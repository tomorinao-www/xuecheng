package link.tomorinao.xuecheng;


import link.tomorinao.xuecheng.auth.mapper.XcUserMapper;
import link.tomorinao.xuecheng.auth.model.po.XcUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Enumeration;

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

    @Test
    void testHost() throws Exception {
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        System.out.println("hostAddress = " + hostAddress);
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                    System.out.println("服务器的IP地址是：" + address.getHostAddress());
//                if (!address.isLoopbackAddress() && !address.isLinkLocalAddress() && address.isSiteLocalAddress()) {
//                }
            }
        }
    }
}
