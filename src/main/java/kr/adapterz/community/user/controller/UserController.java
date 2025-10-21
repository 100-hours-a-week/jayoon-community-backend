package kr.adapterz.community.user.controller;

import static kr.adapterz.community.common.message.SuccessCode.USER_CREATE_SUCCESS;

import jakarta.validation.Valid;
import kr.adapterz.community.common.response.ApiResponseDto;
import kr.adapterz.community.user.dto.CreateUserRequest;
import kr.adapterz.community.user.dto.UserResponse;
import kr.adapterz.community.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    /**
     * 회원가입
     */
    @PostMapping
    public ResponseEntity<ApiResponseDto<UserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        UserResponse newUser = userService.signup(request);
        ApiResponseDto<UserResponse> responseBody = ApiResponseDto.success(newUser,
                USER_CREATE_SUCCESS.getMessage());
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
}
