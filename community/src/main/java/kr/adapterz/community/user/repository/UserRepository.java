package kr.adapterz.community.user.repository;

import kr.adapterz.community.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    public boolean existsByNickname(String nickname);
}
