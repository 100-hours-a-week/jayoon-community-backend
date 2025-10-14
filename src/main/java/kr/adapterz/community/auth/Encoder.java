package kr.adapterz.community.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Encoder {
    private final PasswordEncoder passwordEncoder;

    /**
     * 패스워드를 암호화 합니다. 기본적으로 bcrypt를 사용합니다.
     *
     * 순수한 bcrypt 암호문은 60자입니다. 하지만 SpringSecurity는 인코딩할 때 어떠한 암호화 알고리즘을 사용했는지 알기 위해 접두사를 붙입니다.
     * {접두사} + {암호화 된 값}
     * bcrypt 알고리즘 ID 8자와 암호화 된 값 60자를 해서 총 68자를 저장합니다.
     * 이후 bcrypt 알고리즘이 취약해질 수 있으므로 유동적인 DB 설계가 필요합니다.
     */
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * 로그인 시 요청한 비밀번호와 DB에 저장되 인코딩된 비밀번호 값을 비교합니다.
     */
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
