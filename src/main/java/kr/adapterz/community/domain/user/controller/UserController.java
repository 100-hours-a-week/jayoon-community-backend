package kr.adapterz.community.domain.user.controller;

import static kr.adapterz.community.common.message.SuccessCode.USER_CREATE_SUCCESS;
import static kr.adapterz.community.common.message.SuccessCode.USER_DELETE_SUCCESS;
import static kr.adapterz.community.common.message.SuccessCode.USER_UPDATE_SUCCESS;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.adapterz.community.common.response.ApiResponseDto;
import kr.adapterz.community.common.security.auth.AuthManager;
import kr.adapterz.community.common.web.annotation.LoginUser;
import kr.adapterz.community.domain.user.dto.UserCreateRequestDto;
import kr.adapterz.community.domain.user.dto.UserDeleteRequestDto;
import kr.adapterz.community.domain.user.dto.UserResponseDto;
import kr.adapterz.community.domain.user.dto.UserUpdateRequestDto;
import kr.adapterz.community.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final AuthManager authManager;

    /**
     * 회원가입
     */
    @PostMapping
    public ResponseEntity<ApiResponseDto<UserResponseDto>> createUser(
            @Valid @RequestBody UserCreateRequestDto request) {
        UserResponseDto newUser = userService.signup(request);
        ApiResponseDto<UserResponseDto> responseBody = ApiResponseDto.success(newUser,
                USER_CREATE_SUCCESS.getMessage());
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    /**
     * 회원정보 수정
     *
     * 비밀번호 수정을 성공적으로 진행했다면, 새로운 인증 정보를 생성합니다.
     */
    @PatchMapping("/me")
    public ResponseEntity<ApiResponseDto<?>> updateUser(
            @LoginUser Long userId,
            @Valid @RequestBody UserUpdateRequestDto request,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        userService.updateUser(userId, request);

        if (request.currentPassword() != null && !request.currentPassword().isBlank() &&
                request.updatedPassword() != null && !request.updatedPassword().isBlank()) {
            authManager.login(userId, httpServletRequest, httpServletResponse);
        }

        ApiResponseDto<?> responseBody = ApiResponseDto.success(null,
                USER_UPDATE_SUCCESS.getMessage());
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    /**
     * 회원탈퇴
     *
     * 유저 정보를 삭제 후 인증 정보를 제거합니다.
     */
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponseDto<?>> deleteUser(
            @LoginUser Long userId,
            @Valid @RequestBody UserDeleteRequestDto request,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        userService.deleteUser(userId, request);
        authManager.logout(httpServletRequest, httpServletResponse);

        ApiResponseDto<?> responseBody = ApiResponseDto.success(null,
                USER_DELETE_SUCCESS.getMessage());
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
}
