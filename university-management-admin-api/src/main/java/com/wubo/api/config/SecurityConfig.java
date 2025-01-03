package com.wubo.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF 保护，因为这可能是一个使用无状态认证的 API
                .csrf(csrf -> csrf.disable())

                // 配置授权规则
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll()
                )

        // 可选：添加其他配置，如 HTTP Basic、OAuth2 等
        // 例如，启用 HTTP Basic 认证：
        //.httpBasic(Customizer.withDefaults())

        // 您还可以在这里配置会话管理、异常处理等

        ;

        return http.build();
    }
}
