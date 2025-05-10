package korzeniowski.mateusz.app.model.token;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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

    public String getFormattedExpiryDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "d MMMM yyyy, HH:mm", new Locale("pl", "PL"));
        return expiryDate.format(formatter);
    }

    public Boolean isExpired() {
        return expiryDate.isAfter(LocalDateTime.now());
    }

    public static TokenExpiryDateDto map(PasswordToken token) {
        return new TokenExpiryDateDto(token.getExpiryDate());
    }
}
