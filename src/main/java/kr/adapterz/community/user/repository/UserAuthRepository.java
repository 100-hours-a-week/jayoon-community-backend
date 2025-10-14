package kr.adapterz.community.user.repository;

import kr.adapterz.community.user.entity.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {
    public boolean existsByEmail(String email);
}
