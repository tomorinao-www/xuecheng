package link.tomorinao.xuecheng.config;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONUtil;
import link.tomorinao.xuecheng.exception.RestErrorResponse;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 资源服务器配置
 *
 * @author vains
 */

@ConfigurationProperties(prefix = "security")
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class ResourceServerConfig {


    @Setter
    private List<String> whitelist;

    /**
     * 配置认证相关的过滤器链
     *
     * @param http Spring Security的核心配置类
     * @return 过滤器链
     */
    @Bean
    public SecurityWebFilterChain defaultSecurityFilterChain(ServerHttpSecurity http) {
        // 禁用csrf与cors
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);
        http.cors(ServerHttpSecurity.CorsSpec::disable);

        // 开启全局验证
        http.authorizeExchange((authorize) -> {
                    // 白名单放行
                    if (CollectionUtil.isNotEmpty(whitelist)) {
                        authorize.pathMatchers(Convert.toStrArray(whitelist)).permitAll();
                    }
                    // 全部需要认证
                    authorize.anyExchange().authenticated();
                }
        );

        // 开启OAuth2登录
//        http.oauth2Login(Customizer.withDefaults());

        // 设置当前服务为资源服务，解析请求头中的token
        http.oauth2ResourceServer((resourceServer) -> resourceServer
                        // 使用jwt
                        .jwt(jwt -> jwt
                                // 请求中携带token访问时会触发该解析器适配器
                                .jwtAuthenticationConverter(grantedAuthoritiesExtractor())
                        )
                        // xhr请求未携带Token处理
                        .authenticationEntryPoint(this::authenticationEntryPoint)
//                // 权限不足处理
//                .accessDeniedHandler(this::accessDeniedHandler)
//                // Token解析失败处理
//                .authenticationFailureHandler(this::failureHandler)

        );

        return http.build();
    }

    private Mono<Void> authenticationEntryPoint(
            ServerWebExchange serverWebExchange, AuthenticationException e) {
        e.printStackTrace();
        String errmsg = e.getMessage();
        if (e instanceof InvalidBearerTokenException) {
            errmsg = e.getMessage();
        } else if (e instanceof AuthenticationCredentialsNotFoundException) {
            errmsg = "未携带token";
        }

        return buildReturnMono(errmsg, serverWebExchange);
    }

    private Mono<Void> buildReturnMono(String error, ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        String jsonString = JSONUtil.toJsonStr(new RestErrorResponse(error));

        byte[] bits = jsonString.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }

    /**
     * 自定义jwt解析器，设置解析出来的权限信息的前缀与在jwt中的key
     *
     * @return jwt解析器适配器 ReactiveJwtAuthenticationConverterAdapter
     */
    public Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        // 设置解析权限信息的前缀，设置为空是去掉前缀
        grantedAuthoritiesConverter.setAuthorityPrefix("");
        // 设置权限信息在jwt claims中的key
        grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

}
