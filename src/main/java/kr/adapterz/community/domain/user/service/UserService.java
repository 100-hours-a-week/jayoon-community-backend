package kr.adapterz.community.domain.user.service;

import static kr.adapterz.community.common.message.ErrorCode.PASSWORD_MISMATCH;
import static kr.adapterz.community.common.message.ErrorCode.USER_AUTH_NOT_FOUND;
import static kr.adapterz.community.common.message.ErrorCode.USER_EMAIL_ALREADY_EXISTED;
import static kr.adapterz.community.common.message.ErrorCode.USER_NICKNAME_ALREADY_EXISTED;
import static kr.adapterz.community.common.message.ErrorCode.USER_NOT_FOUND;

import kr.adapterz.community.common.exception.dto.BadRequestException;
import kr.adapterz.community.common.exception.dto.NotFoundException;
import kr.adapterz.community.common.security.encoding.Encoder;
import kr.adapterz.community.domain.user.dto.UserCreateRequestDto;
import kr.adapterz.community.domain.user.dto.UserDeleteRequestDto;
import kr.adapterz.community.domain.user.dto.UserResponseDto;
import kr.adapterz.community.domain.user.dto.UserUpdateRequestDto;
import kr.adapterz.community.domain.user.entity.User;
import kr.adapterz.community.domain.user.entity.UserAuth;
import kr.adapterz.community.domain.user.repository.UserAuthRepository;
import kr.adapterz.community.domain.user.repository.UserRepository;
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
    public UserResponseDto signup(UserCreateRequestDto request) {
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

        return UserResponseDto.of(savedUser, request.email());
    }

    /**
     * 브라우저가 저장할 유저의 최소 정보를 반환합니다.
     */
    public UserResponseDto getUserInfoById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
        UserAuth userAuth = userAuthRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException(USER_AUTH_NOT_FOUND));

        return UserResponseDto.of(user, userAuth.getEmail());
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

    /**
     * 회원정보를 수정합니다.
     *
     * 닉네임, 프로필 이미지 URL, 비밀번호를 수정할 수 있습니다.
     * JPA의 Dirty checking은 @Transactional 내에 있는 엔티티 변경을 추적하여 자동으로 쿼리를 생성합니다.
     * 때문에 명시적으로 update 쿼리를 작성하지 않아도 됩니다.
     */
    @Transactional
    public void updateUser(Long userId, UserUpdateRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
        UserAuth userAuth = userAuthRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(USER_AUTH_NOT_FOUND));

        if (request.nickname() != null && !request.nickname().isBlank()) {
            if (userRepository.existsByNickname(request.nickname()) && !user.getNickname()
                    .equals(request.nickname())) {
                throw new BadRequestException(USER_NICKNAME_ALREADY_EXISTED);
            }
            user.updateNickname(request.nickname());
        }

        user.updateProfileImageUrl(request.profileImageUrl());

        if (request.currentPassword() != null && !request.currentPassword().isBlank() &&
                request.updatedPassword() != null && !request.updatedPassword().isBlank()) {
            if (!encoder.checkPassword(request.currentPassword(), userAuth.getPasswordHash())) {
                throw new BadRequestException(PASSWORD_MISMATCH);
            }
            String newPasswordHash = encoder.encodePassword(request.updatedPassword());
            userAuth.updatePasswordHash(newPasswordHash);
        }
    }

    /**
     * 회원탈퇴를 진행합니다.
     *
     * 현재 비밀번호와 일치하는지 확인하고, 일치한다면 해당 userId의 User와 UserAuth의 soft delete를 진행합니다.
     */
    @Transactional
    public void deleteUser(Long userId, UserDeleteRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
        UserAuth userAuth = userAuthRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(USER_AUTH_NOT_FOUND));

        if (!encoder.checkPassword(request.password(), userAuth.getPasswordHash())) {
            throw new BadRequestException(PASSWORD_MISMATCH);
        }

        user.deleteSoftly();
        userAuth.deleteSoftly();
    }
}