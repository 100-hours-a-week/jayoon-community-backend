package kr.adapterz.community.user.service;

import static kr.adapterz.community.common.message.ErrorCode.USER_AUTH_NOT_FOUND;
import static kr.adapterz.community.common.message.ErrorCode.USER_EMAIL_ALREADY_EXISTED;
import static kr.adapterz.community.common.message.ErrorCode.USER_NICKNAME_ALREADY_EXISTED;
import static kr.adapterz.community.common.message.ErrorCode.USER_NOT_FOUND;

import kr.adapterz.community.common.exception.BadRequestException;
import kr.adapterz.community.common.exception.NotFoundException;
import kr.adapterz.community.security.Encoder;
import kr.adapterz.community.security.jwt.JwtDto;
import kr.adapterz.community.security.jwt.JwtManager;
import kr.adapterz.community.user.dto.CreateUserRequestDto;
import kr.adapterz.community.user.dto.UserResponseDto;
import kr.adapterz.community.user.entity.User;
import kr.adapterz.community.user.entity.UserAuth;
import kr.adapterz.community.user.repository.UserAuthRepository;
import kr.adapterz.community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserAuthRepository userAuthRepository;
    private final Encoder encoder;
    private final JwtManager jwtManager;

    /**
     * 유저를 생성합니다.
     */
    @Transactional
    public UserResponseDto signup(CreateUserRequestDto request) {
        if (userAuthRepository.existsByEmail((request.email()))) {
            throw new BadRequestException(USER_EMAIL_ALREADY_EXISTED);
        }
        if (userRepository.existsByNickname((request.nickname()))) {
            throw new BadRequestException(USER_NICKNAME_ALREADY_EXISTED);
        }

        // auto increment PK이므로 DB에 저장을 해야 생성 됨
        User newUser = User.from(request);
        // PK 생성 됨
        User savedUser = userRepository.save(newUser);

        String passwordHash = encoder.encodePassword(request.password());
        UserAuth newUserAuth = UserAuth.of(
                savedUser,
                request.email(),
                passwordHash
        );
        userAuthRepository.save(newUserAuth);

        JwtDto jwtDto = jwtManager.generateToken(savedUser.getId());
        return UserResponseDto.of(savedUser, request.email(), jwtDto);
    }

    /**
     * 브라우저가 저장할 유저의 최소 정보를 반환합니다.
     */
    public UserResponseDto getUserInfoById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
        UserAuth userAuth = userAuthRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException(USER_AUTH_NOT_FOUND));

        JwtDto jwtDto = jwtManager.generateToken(id);
        return UserResponseDto.of(user, userAuth.getEmail(), jwtDto);
    }

    /**
     * 유저 Entity를 반환합니다.
     */
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }

    public UserAuth findUserAuthByEmail(String email) {
        return userAuthRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }
}
