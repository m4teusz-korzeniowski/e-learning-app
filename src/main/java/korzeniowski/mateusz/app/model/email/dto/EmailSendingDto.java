package korzeniowski.mateusz.app.model.email.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public class EmailSendingDto {
    @Valid
    @NotEmpty(message = "*pole nie może być puste!")
    private List<@Email String> to;
    @NotBlank(message = "*pole nie może być puste!")
    @Size(max = 500, message = "*przekroczono maksymalny rozmiar tematu!")
    private String subject;
    @NotBlank(message = "*pole nie może być puste!")
    @Size(max = 75000, message = "*przekroczono maksymalny rozmiar wiadomości!")
    private String text;

    public List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
