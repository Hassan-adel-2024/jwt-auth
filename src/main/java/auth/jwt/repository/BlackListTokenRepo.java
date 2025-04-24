package auth.jwt.repository;

import auth.jwt.entity.BlackListToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface BlackListTokenRepo extends JpaRepository<BlackListToken, Long> {
    boolean existsByToken(String token);
    void deleteByExpiryDateBefore(LocalDateTime dateTime);
}
