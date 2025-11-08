package kr.adapterz.community.user.service;

import static kr.adapterz.community.common.message.ErrorCode.USER_EMAIL_ALREADY_EXISTED;
import static kr.adapterz.community.common.message.ErrorCode.USER_NICKNAME_ALREADY_EXISTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import kr.adapterz.community.common.exception.dto.BadRequestException;
import kr.adapterz.community.common.security.encoding.Encoder;
import kr.adapterz.community.domain.user.dto.CreateUserRequestDto;
import kr.adapterz.community.domain.user.dto.UserResponseDto;
import kr.adapterz.community.domain.user.entity.User;
import kr.adapterz.community.domain.user.entity.UserAuth;
import kr.adapterz.community.domain.user.repository.UserAuthRepository;
import kr.adapterz.community.domain.user.repository.UserRepository;
import kr.adapterz.community.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class) // JUnit에서 Mockito를 사용하기 위한 확장 기능
class UserServiceTest {

    @InjectMocks // @Mock으로 생성된 가짜 객체들을 이 클래스에 주입합니다.
    private UserService userService;

    @Mock // @InjectMocks에 의해 주입 될 가짜 객체 생성
    private UserRepository userRepository;

    @Mock
    private UserAuthRepository userAuthRepository;

    @Mock
    private Encoder encoder;

//    @Mock
//    private JwtManager jwtManager;

    @DisplayName("회원가입 성공 테스트")
    @Test
    void 회원가입_성공() {
        // given (조건이 주어지고,)
        CreateUserRequestDto request = new CreateUserRequestDto("test@email.com", "password123",
                "testuser", "url");
        User savedUser = User.from(request);
        ReflectionTestUtils.setField(savedUser, "id", 1L); // savedUser 객체의 'id' 필드에 1L 값을 강제로 주입
        // Mock 객체의 행동 정의
        // BDD(Behavior Driven Development)를 따라 mockito 기본 API인 when이 아닌 wrapping한 given을 사용합니다.
        given(userAuthRepository.existsByEmail(request.email())).willReturn(false);
        given(userRepository.existsByNickname(request.nickname())).willReturn(false);
        given(encoder.encodePassword(request.password())).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willReturn(savedUser);

        // when (무엇을 할 때)
        // @InjectMocks으로 인해 Mock 객체가 주입된 UserService::createUser를 실행하게 됩니다.
        // given()에서 주어진 값을 만났을 때 실제로 실행되지 않고, willReturn에서 정의한 값으로 대체됩니다.
        UserResponseDto response = userService.signup(request);

        // then (결과는 이래야 한다)
        // assertThat은 상태를 검증한다.
        // verify는 행위를 검증한다.
        // 기본 타입은 상관 없지만, 사용자 지정 타입, class들은 equals를 오버라이딩 해야 내부 필드의 값을 올바르게 비교 가능합니다. 하지 않으면
        assertThat(response.userId()).isEqualTo(savedUser.getId());
        assertThat(response.nickname()).isEqualTo(savedUser.getNickname());
        assertThat(response.nickname()).isEqualTo(request.nickname());
        assertThat(response.profileImageUrl()).isEqualTo(savedUser.getProfileImageUrl());
        // verify는 when에서 호출한 기록을 보고 검증합니다.
        // @Mock이 Proxy 객체를 만들어 호출을 가로채고, 횟수를 기록합니다.
        // verify는 Mock 객체를 반환합니다.
        // How to: verify(①호출되었는지 검증할 Mock 객체, ②검증 조건).③호출되었어야 하는 메서드();
        // any()는 어떠한 값이 들어있든 상관 없이 해당 타입의 객체로 매핑해줍니다.
        // userRepository의 save가 User 객체를 인자로 1번 호출되었는지 검증
        verify(userRepository, times(1)).save(any(User.class));
        // userAuthRepository의 save가 UserAuth 객체를 인자로 1번 호출되었는지 검증
        verify(userAuthRepository, times(1)).save(any(UserAuth.class));
    }

    @DisplayName("회원가입_실패_이메일_중복")
    @Test
    void 회원가입_실패_이메일_중복() {
        // given
        CreateUserRequestDto request = new CreateUserRequestDto("test@email.com", "password123",
                "testuser", "url");
        given(userAuthRepository.existsByEmail(request.email())).willReturn(true);

        // when
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userService.signup(request);
        });

        // then
        assertThat(exception.getMessage()).isEqualTo(USER_EMAIL_ALREADY_EXISTED.getMessage());
    }

    @DisplayName("회원가입_실패_닉네임_중복")
    @Test
    void 회원가입_실패_닉네임_중복() {
        // given
        CreateUserRequestDto request = new CreateUserRequestDto("test@email.com", "password123",
                "testuser", "url");
        given(userAuthRepository.existsByEmail(request.email())).willReturn(false);
        given(userRepository.existsByNickname(request.nickname())).willReturn(true);

        // when
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userService.signup(request);
        });

        // then
        assertThat(exception.getMessage()).isEqualTo(USER_NICKNAME_ALREADY_EXISTED.getMessage());
    }
}