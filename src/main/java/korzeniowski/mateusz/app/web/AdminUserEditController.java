package korzeniowski.mateusz.app.web;

import jakarta.validation.Valid;
import korzeniowski.mateusz.app.email.EmailService;
import korzeniowski.mateusz.app.exceptions.EmailAlreadyInUseException;
import korzeniowski.mateusz.app.exceptions.PeselAlreadyInUseException;
import korzeniowski.mateusz.app.exceptions.UserEnabledException;
import korzeniowski.mateusz.app.model.token.TokenExpiryDateDto;
import korzeniowski.mateusz.app.model.user.User;
import korzeniowski.mateusz.app.model.user.dto.UserSettingsDto;
import korzeniowski.mateusz.app.service.PasswordTokenService;
import korzeniowski.mateusz.app.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Optional;

@Controller
public class AdminUserEditController {

    private final UserService userService;
    private final PasswordTokenService passwordTokenService;

    public AdminUserEditController(UserService userService, PasswordTokenService passwordTokenService) {
        this.userService = userService;
        this.passwordTokenService = passwordTokenService;
    }

    private void setUserInitialData(User userData, UserSettingsDto user) {
        user.setId(userData.getId());
        user.setEmail(userData.getEmail());
        user.setFirstName(userData.getFirstName());
        user.setLastName(userData.getLastName());
        user.setPesel(userData.getPesel());
        user.setEnabled(userData.getEnabled());
        if (userData.getGroup() != null) {
            user.setGroup(userData.getGroup().getName());
        } else {
            user.setGroup("Brak");
        }
        user.setRole(userData.getUserRoles().stream().toList().get(0).getName());
    }

    @GetMapping("/admin/users/edit/{id}")
    public String showUserEditForm(@PathVariable long id, @ModelAttribute("user") UserSettingsDto user,
                                   @ModelAttribute("token") TokenExpiryDateDto token) {
        Optional<User> userData = userService.findUserById(id);
        if (userData.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            setUserInitialData(userData.get(), user);
            passwordTokenService.findTokenExpiryDateByUserId(userData.get().getId()).ifPresent(expiryDate -> {
                token.setExpiryDate(expiryDate.getExpiryDate());
            });
        }
        return "user-edit";
    }

    private String returnUserEditForm(Model model, UserSettingsDto user, long userId) {
        model.addAttribute("user", user);
        model.addAttribute("id", userId);
        return "user-edit";
    }

    @PostMapping("/admin/users/edit/{id}")
    public String editUser(@PathVariable long id, @ModelAttribute("user") @Valid UserSettingsDto user,
                           BindingResult bindingResult, RedirectAttributes redirectAttributes,
                           @ModelAttribute("token") TokenExpiryDateDto token) {
        if (bindingResult.hasErrors()) {
            return showUserEditForm(id, user, token);
        }
        try {
            userService.updateUser(user);
            redirectAttributes.addFlashAttribute("message",
                    "Zmiana zakończyła się sukcesem");
        } catch (EmailAlreadyInUseException e) {
            bindingResult.rejectValue("email", "error.email", e.getMessage());
            return showUserEditForm(id, user, token);
        } catch (PeselAlreadyInUseException e) {
            bindingResult.rejectValue("pesel", "error.pesel", e.getMessage());
            return showUserEditForm(id, user, token);
        } catch (UserEnabledException e) {
            bindingResult.rejectValue("enabled", "error.user.enabled", e.getMessage());
            return showUserEditForm(id, user, token);
        }
        return "redirect:/admin/users/edit/" + id;
    }


    private void resendToken(User user, String principalName) {
        userService.sendActivationEmail(user, principalName);
    }

    @PostMapping("/admin/users/resend-token")
    String resendRegistrationToken(@RequestParam(name = "userId") long userId,
                                   RedirectAttributes redirectAttributes, Principal principal) {

        Optional<User> user = userService.findUserById(userId);
        if (user.isEmpty()) {
            String message = "*nie znaleziono użytkownika o podanym id!";
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/admin/users/edit/" + userId;
        } else {
            if (user.get().getPassword() == null) {
                resendToken(user.get(), principal.getName());
                String message = "Ponownie wysłano wiadomość e-mail z linkiem aktywacyjnym do: "
                        + user.get().getEmail() + ".";
                redirectAttributes.addFlashAttribute("tokenMessage", message);
                return "redirect:/admin/users/edit/" + userId;
            } else {
                String message = "*użytkownik dokonał już rejestracji!";
                redirectAttributes.addFlashAttribute("error", message);
                return "redirect:/admin/users/edit/" + userId;
            }
        }
    }
}
