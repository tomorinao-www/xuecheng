package link.tomorinao.xuecheng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/*
springboot3.1 自动装配中创建RestClientTransport的构造方法调用与es8.9.x之后定义的构造方法不匹配，
具体而言是第三个参数类型变了
解决方法：
1.排除自动装配
https://discuss.elastic.co/t/caused-by-java-lang-nosuchmethoderror-void-co-elastic-clients-transport-rest-client-restclienttransport-init/339573/9
2.定义自己的RestClientTransport Bean
https://github.com/spring-projects/spring-boot/issues/36669
 */
@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchClientAutoConfiguration.class})
public class SearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchApplication.class, args);
    }

}
