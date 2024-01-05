package link.tomorinao.xuecheng.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class GlobalCorsFilter {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        /*
        当setAllowCredentials(true)时，不可以addAllowedOrigin("*")，
        因为无法把"Access-Control-Allow-Origin"响应头设置为"*",
        可以选择：（1）列出具体的允许跨域源。（2）使用addAllowedOriginPattern
         */
//        config.addAllowedOrigin("*"); // 允许所有源
        config.addAllowedOriginPattern("*"); // 允许所有源
        config.addAllowedHeader("*"); // 放行所有请求头
        config.addAllowedMethod("*"); //允许所有请求方法
        config.setAllowCredentials(true); // 允许跨域发送cookie
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
