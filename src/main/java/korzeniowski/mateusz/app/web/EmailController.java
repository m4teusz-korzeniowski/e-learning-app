package korzeniowski.mateusz.app.web;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import korzeniowski.mateusz.app.email.EmailRecipientCacheService;
import korzeniowski.mateusz.app.email.EmailService;
import korzeniowski.mateusz.app.model.email.dto.EmailSendingDto;
import korzeniowski.mateusz.app.model.user.dto.UserSessionDto;
import korzeniowski.mateusz.app.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class EmailController {

    private final EmailService emailService;
    private final EmailRecipientCacheService emailRecipientCacheService;
    private final UserService userService;

    public EmailController(EmailService emailService, EmailRecipientCacheService emailRecipientCacheService, UserService userService) {
        this.emailService = emailService;
        this.emailRecipientCacheService = emailRecipientCacheService;
        this.userService = userService;
    }

    private String redirectWithRecipients(HttpSession session, List<String> recipients) {
        String recipientsId = emailRecipientCacheService.storeRecipients(session, recipients);
        return "redirect:/email?recipientsId=" + recipientsId;
    }

    @GetMapping("/email/prepare-all")
    public String prepareMessageRecipients(HttpSession session) {
        List<String> recipients = userService.findAllUserEmails();
        return redirectWithRecipients(session, recipients);
    }

    @GetMapping("/email/prepare-course")
    public String prepareCourseRecipients(HttpSession session,
                                          @RequestParam(name = "courseId") long courseId) {
        List<String> recipients = userService.findUsersEmailsByCourseId(courseId);
        return redirectWithRecipients(session, recipients);
    }

    @GetMapping("/email/prepare-group")
    public String prepareGroupRecipients(HttpSession session,
                                         @RequestParam(name = "groupId") long groupId) {
        List<String> recipients = userService.findUserEmailsByGroupId(groupId);
        return redirectWithRecipients(session, recipients);
    }

    @GetMapping("/email/prepare-user")
    public String prepareSingleRecipient(HttpSession session,
                                         @RequestParam(name = "userId") long userId) {
        List<String> recipients = userService.findUserEmailById(userId);
        return redirectWithRecipients(session, recipients);
    }

    @GetMapping("/email")
    public String showEmailForm(@RequestParam(name = "recipientsId", required = false) String recipientsId
            , HttpSession session, Model model) {
        List<String> recipients = new ArrayList<>();
        if (recipientsId != null) {
            recipients = emailRecipientCacheService.getRecipients(session, recipientsId);
        }
        EmailSendingDto email = new EmailSendingDto();
        email.setTo(recipients);
        model.addAttribute("email", email);
        return "email-form";
    }


    @PostMapping("/email")
    public String sendEmail(@ModelAttribute(name = "email") @Valid EmailSendingDto email, BindingResult bindingResult,
                            Principal principal, Model model) {
        boolean hasEmailErrors = bindingResult.getFieldErrors().stream()
                .anyMatch(error -> error.getField().startsWith("to[") && "Email".equals(error.getCode()));
        if (hasEmailErrors) {
            bindingResult.rejectValue("to", "error.to",
                    "*co najmniej jeden z podanych adresów e-mail, jest niepoprawny!");
        }
        if(email.getText().equals("<p><br></p>")){
            bindingResult.rejectValue("text", "error.text",
                    "*wiadomość nie może być pusta!");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("email", email);
            return "email-form";
        }
        try {
            emailService.sendHtmlMessage(
                    email.getTo(),
                    email.getSubject(),
                    email.getText(),
                    principal.getName()
            );
        } catch (MessagingException e) {
            bindingResult.rejectValue("to", "error.to", e.getMessage());
        }
        return "redirect:/email/success";
    }

    @GetMapping("/email/success")
    public String emailConfirmation() {
        return "email-confirmation";
    }
}
