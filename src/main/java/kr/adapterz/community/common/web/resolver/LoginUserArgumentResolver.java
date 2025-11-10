package kr.adapterz.community.common.web.resolver;

import jakarta.servlet.http.HttpServletRequest;
import kr.adapterz.community.common.web.annotation.LoginUser;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {
    /**
     * 파라미터를 검사합니다.
     *
     * 1. @LoginUser 어노테이션이 붙어있는가?
     * 2. 파라미터의 타입이 Long인가?
     *
     * @param parameter the method parameter to check
     * @return 어노테이션 타입이 LoginUser이고, 파라미터 타입이 Long이면 참을, 아니라면 false를 반환합니다.
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasParameterAnnotation = parameter.hasParameterAnnotation(LoginUser.class);
        boolean isLongType = parameter.getParameterType().equals(Long.class);
        return hasParameterAnnotation && isLongType;
    }

    /**
     * 요청 attribute에서 userId를 추출하여 반환합니다.
     *
     * 요청 응답 생성 후, userId attribute 추출
     * 존재하지 않으면 null 반환, userId 반환
     *
     * @param parameter     the method parameter to resolve. This parameter must have previously
     *                      been passed to {@link #supportsParameter} which must have returned
     *                      {@code true}.
     * @param mavContainer  the ModelAndViewContainer for the current request
     * @param webRequest    the current request
     * @param binderFactory a factory for creating {@link WebDataBinder} instances
     * @return userId를 반환합니다.
     * @throws Exception
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory)
            throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        return request.getAttribute("userId");
    }
}
