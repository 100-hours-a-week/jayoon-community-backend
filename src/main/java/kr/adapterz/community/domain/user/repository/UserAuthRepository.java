package kr.adapterz.community.domain.user.repository;

import java.util.Optional;
import kr.adapterz.community.domain.user.entity.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {
    boolean existsByEmail(String email);

    Optional<UserAuth> findByEmail(String email);
}
