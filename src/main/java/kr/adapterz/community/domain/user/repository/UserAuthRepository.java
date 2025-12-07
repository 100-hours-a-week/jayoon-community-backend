package kr.adapterz.community.domain.user.repository;

import java.util.Optional;
import kr.adapterz.community.domain.user.entity.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {
    boolean existsByEmail(String email);

    Optional<UserAuth> findByEmail(String email);

    /**
     * email과 일치하고, 탈퇴하지 않은 user_auth 엔티티를 조회합니다.
     *
     * deleted_at이 NULL이면 정상 유저이며, NULL이면 탈퇴한 유저입니다.
     *
     * @param email
     * @return
     */
    Optional<UserAuth> findByEmailAndDeletedAtIsNull(String email);

    Optional<UserAuth> findByUserId(Long userId);
}
