package kr.adapterz.community.user;

import kr.adapterz.community.common.response.dto.ApiResponse;
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

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody CreateUserRequest request) {
        UserResponse newUser = userService.createUser(request);
        ApiResponse<UserResponse> responseBody = ApiResponse.success(newUser, "회원가입을 성공했습니다.");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
}
