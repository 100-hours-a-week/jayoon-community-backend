package kr.adapterz.community.user.service;

import kr.adapterz.community.auth.Encoder;
import kr.adapterz.community.common.response.exception.BadRequestException;
import kr.adapterz.community.common.response.exception.NotFoundException;
import kr.adapterz.community.user.dto.CreateUserRequest;
import kr.adapterz.community.user.dto.UserResponse;
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

    /**
     * 유저를 생성합니다.
     */
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userAuthRepository.existsByEmail((request.email()))) {
            throw new BadRequestException("이메일이 이미 존재합니다.");
        }
        if (userRepository.existsByNickname((request.nickname()))) {
            throw new BadRequestException("닉네임이 이미 존재합니다.");
        }

        User newUser = User.from(request);
        User savedUser = userRepository.save(newUser);

        String passwordHash = encoder.encodePassword(request.password());
        UserAuth newUserAuth = UserAuth.of(
                savedUser,
                request.email(),
                passwordHash
        );
        userAuthRepository.save(newUserAuth);

        return UserResponse.of(savedUser, request.email());
    }

    /**
     * 브라우저가 저장할 유저의 최소 정보를 반환합니다.
     */
    public UserResponse getUserInfoById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("유저가 존재하지 않습니다."));
        UserAuth userAuth = userAuthRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("유저 인증 정보가 존재하지 않습니다."));
        return UserResponse.of(user, userAuth.getEmail());
    }

    /**
     * 유저 Entity를 반환합니다.
     */
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("유저가 존재하지 않습니다."));
    }
}
