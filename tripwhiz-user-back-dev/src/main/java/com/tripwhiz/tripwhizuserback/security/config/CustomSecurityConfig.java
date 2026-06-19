package com.tripwhiz.tripwhizuserback.security.config;


import com.tripwhiz.tripwhizuserback.security.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import java.util.List;

@Configuration
// @PreAuthorize 등의 메서드 보안 어노테이션을 활성화
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class CustomSecurityConfig {

    private final JWTUtil jwtUtil;

    // 보안 필터 체인 설정 메서드
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 기본 로그인 화면을 비활성화하여 직접 로그인 UI를 제공하거나 토큰 인증을 사용하도록 설정
        http.formLogin(config -> config.disable());

        // 세션을 생성하지 않도록 설정하여, 세션을 통한 인증이 아닌 JWT 토큰만을 사용하도록
        http.sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.NEVER));

        // CSRF 보호를 비활성화합니다. 일반적으로 API 서버에서는 CSRF가 필요하지 않음
        http.csrf(config -> config.disable());

        // JWTCheckFilter를 UsernamePasswordAuthenticationFilter 이전에 추가하여,
        // JWT 토큰을 통한 인증을 필터 체인 초기에 처리하도록
//        http.addFilterBefore(new JWTCheckFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        // CORS 설정을 적용하여, 외부 도메인에서의 API 요청을 허용
        http.cors(cors -> {
            cors.configurationSource(corsConfigurationSource());
        });

//        // COOP, COEP 헤더 추가
//        http.headers(headers -> {
//            headers.addHeaderWriter(new StaticHeadersWriter("Cross-Origin-Opener-Policy", "same-origin-allow-popups"));
//            headers.addHeaderWriter(new StaticHeadersWriter("Cross-Origin-Embedder-Policy", "require-corp"));
//        });
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));



        http.authorizeHttpRequests(auth -> {
            auth
                    .requestMatchers("/").permitAll() // /health 경로는 인증 없이 허용
                    .anyRequest().anonymous();         // 다른 모든 경로는 인증 필요
        });
        return http.build();
    }

    // CORS 설정 메서드
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOriginPatterns(List.of("")); // 모든 출처에서의 요청을 허용
//        corsConfiguration.setAllowedOriginPatterns(List.of("https://tripwhiz.store", "https://tripwhiz.shop"));
        corsConfiguration.setAllowedOriginPatterns(List.of("http://localhost:3000", "http://localhost:5173"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type", "email"));
        corsConfiguration.setAllowCredentials(true);

        // 설정한 CORS 정책을 모든 경로에 적용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }

}


