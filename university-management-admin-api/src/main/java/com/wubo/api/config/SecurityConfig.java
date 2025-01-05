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
                // 禁用 CSRF 保护
                .csrf(csrf -> csrf.disable())

                // 配置跨域
                .cors(Customizer.withDefaults())

                // 配置授权规则
                .authorizeHttpRequests(auth -> auth
                        // 允许静态资源访问
                        .requestMatchers("/files/**").permitAll()
                        .requestMatchers("/upload/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}
