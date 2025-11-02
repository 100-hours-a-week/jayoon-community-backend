package kr.adapterz.community.common.config;

import kr.adapterz.community.common.security.auth.AuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final AuthenticationFilter authenticationFilter;

    /**
     * 모든 핸들러 요청에서 인증을 진행할 AuthenticationFilter를 필터로 등록합니다.
     *
     * @return
     * @Bean이 붙은 메서드는 자동으로 해당 메서드 이름을 하는 빈이 생성 됩니다. 이는 authenticationFilter를 필터에 등록하는 빈 생성
     */
    @Bean
    FilterRegistrationBean<AuthenticationFilter> authenticationFilterRegistrationBean() {
        FilterRegistrationBean<AuthenticationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(authenticationFilter);
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }

    /**
     * 사진과 같은 리소스 요청을 할 때 기본 경로를 설정합니다.
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:./uploads/images/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 60초 * 60 = 1 hour
        long maxAge = 60 * 60;
        // 모든 경로(/)에 대해 CORS 설정을 적용합니다.
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5500", "http://127.0.0.1:5500") // Live Server
                .allowedOrigins("http://localhost:3000", "http://127.0.0.1:3000") // Local FE server
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")
                .allowedHeaders("*")
                // 쿠키/인증 정보를 요청에 포함할 수 있도록 허용합니다.
                // (httpOnly 쿠키를 사용하려면 true로 설정해야 합니다)
                .allowCredentials(true)
                // 브라우저가 예비 요청(Preflight)의 결과를 캐시할 시간(초)을 설정합니다.
                .maxAge(maxAge);
    }
}
