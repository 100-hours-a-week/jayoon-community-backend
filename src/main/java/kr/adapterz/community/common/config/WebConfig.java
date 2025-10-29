package kr.adapterz.community.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:./uploads/images/");
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 1. 모든 경로(/)에 대해 CORS 설정을 적용합니다.

                // VS Code Live Server의 기본 주소인 5500번 포트를 허용합니다.
                .allowedOrigins("http://localhost:5500", "http://127.0.0.1:5500")
                .allowedOrigins("http://localhost:3000", "http://127.0.0.1:3000")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")
                .allowedHeaders("*")
                // 쿠키/인증 정보를 요청에 포함할 수 있도록 허용합니다.
                // (refreshToken 같은 httpOnly 쿠키를 사용하려면 true로 설정해야 합니다)
                .allowCredentials(true)
                // 브라우저가 예비 요청(Preflight)의 결과를 캐시할 시간(초)을 설정합니다.
                .maxAge(3600);
    }
}
