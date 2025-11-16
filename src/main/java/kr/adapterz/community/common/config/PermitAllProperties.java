package kr.adapterz.community.common.config;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpMethod;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "security.permit-all-paths")
public class PermitAllProperties {
    /**
     * application.yml의 값이 바인딩 됩니다.
     *
     * Pattern Matcher를 사용합니다. 예시는 다음과 같습니다.
     * 예시: 만약 모든 GET 요청을 허용하고 싶다면 , HttpMethod.GET, Arrays.asList("/**")
     * 예시: 게시글 목록 및 상세 조회는 허용하고 싶다면 , HttpMethod.GET, Arrays.asList("/posts", "/posts/*")
     *
     * Key: HttpMethod (e.g., POST, GET 등)
     * Value: 허용할 경로 리스트
     */
    private final Map<HttpMethod, List<String>> paths;
}
