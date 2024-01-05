package link.tomorinao.xuecheng.auth.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class ResourceServerConfig {

    @Bean
    @Order(2)
    SecurityFilterChain filterChain(HttpSecurity http
    ) throws Exception {
        // 禁用csrf与cors
        http.csrf((csrf) -> csrf.disable());
        http.cors((cors) -> cors.disable());
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/r/**", "/home").authenticated()
                        .anyRequest().permitAll()
                )
                .formLogin(formLogin -> formLogin
                        .successForwardUrl("/login-success")
                )
                .logout(logout -> logout.logoutUrl("/logout")
                )
        ;
        // 添加BearerTokenAuthenticationFilter，将认证服务当做一个资源服务，解析请求头中的token
        http
                .oauth2ResourceServer((resourceServer) -> resourceServer
                        .jwt(jwt->jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }

    /**
     * 自定义jwt解析器，设置解析出来的权限信息的前缀与在jwt中的key
     * 要与自定义jwt生成器配套
     * @see AuthServerConfig#jwtTokenCustomizer()
     * @return jwt解析器 JwtAuthenticationConverter
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        // 设置解析权限信息的前缀，设置为空是去掉前缀；默认前缀是SCOPE_
        grantedAuthoritiesConverter.setAuthorityPrefix("");
        // 设置权限信息在jwt claims中的key
        grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}

