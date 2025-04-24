package korzeniowski.mateusz.app.repository;

import korzeniowski.mateusz.app.model.token.PasswordToken;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordTokenRepository extends CrudRepository<PasswordToken, Long> {
    Optional<PasswordToken> findByToken(String token);
    void deleteByExpiryDateBefore(LocalDateTime date);
    @Query("select token from PasswordToken token where token.user.id = :userId")
    Optional<PasswordToken> findByUserId(@Param("userId") Long userId);
}
