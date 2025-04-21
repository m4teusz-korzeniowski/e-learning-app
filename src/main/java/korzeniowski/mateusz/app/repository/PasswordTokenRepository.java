package korzeniowski.mateusz.app.repository;

import korzeniowski.mateusz.app.model.token.PasswordToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PasswordTokenRepository extends CrudRepository<PasswordToken, Long> {
    Optional<PasswordToken> findByToken(String token);
}
