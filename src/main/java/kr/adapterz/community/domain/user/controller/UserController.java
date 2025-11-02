package kr.adapterz.community.domain.user.controller;

import static kr.adapterz.community.common.message.SuccessCode.USER_CREATE_SUCCESS;

import jakarta.validation.Valid;
import kr.adapterz.community.common.response.ApiResponseDto;
import kr.adapterz.community.domain.user.dto.CreateUserRequestDto;
import kr.adapterz.community.domain.user.dto.UserResponseDto;
import kr.adapterz.community.domain.user.service.UserService;
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
    public ResponseEntity<ApiResponseDto<UserResponseDto>> createUser(
            @Valid @RequestBody CreateUserRequestDto request) {
        UserResponseDto newUser = userService.signup(request);
        ApiResponseDto<UserResponseDto> responseBody = ApiResponseDto.success(newUser,
                USER_CREATE_SUCCESS.getMessage());
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
}
