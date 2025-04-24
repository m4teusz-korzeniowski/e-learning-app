package korzeniowski.mateusz.app.model.token;

import java.time.LocalDateTime;

public class TokenExpiryDateDto {
    private LocalDateTime expiryDate;

    public TokenExpiryDateDto(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public static TokenExpiryDateDto map(PasswordToken token) {
        return new TokenExpiryDateDto(token.getExpiryDate());
    }
}
