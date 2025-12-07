package kr.adapterz.community.common.config;

import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Spring Security가 관리하는 클래스입니다.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * Spring security 의존성 추가로 자동으로 인가를 관리하기 때문에 해당 설정을 추가합니다.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
        ;
        return http.build();
    }

    /**
     * CORS 설정 소스: WebConfig의 설정과 유사하게 구성
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(
                Arrays.asList("http://localhost:3000", "https://localhost:3000",
                        "http://guidey.site", "https://guidey.site"));  // 허용 Origin
        configuration.setAllowedMethods(Arrays.asList("*"));  // 모든 메서드 허용 (GET, POST 등)
        configuration.setAllowedHeaders(Arrays.asList("*"));  // 모든 헤더 허용
        configuration.setAllowCredentials(true);  // 쿠키/인증 정보 허용
        configuration.setMaxAge(3600L);  // Preflight 캐시 시간 (1시간)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);  // 모든 경로에 적용
        return source;
    }

}
