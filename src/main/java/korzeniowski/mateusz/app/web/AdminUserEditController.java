package korzeniowski.mateusz.app.web;

import jakarta.validation.Valid;
import korzeniowski.mateusz.app.exceptions.EmailAlreadyInUseException;
import korzeniowski.mateusz.app.exceptions.PeselAlreadyInUseException;
import korzeniowski.mateusz.app.exceptions.UserEnabledException;
import korzeniowski.mateusz.app.model.user.User;
import korzeniowski.mateusz.app.model.user.dto.UserSettingsDto;
import korzeniowski.mateusz.app.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AdminUserEditController {

    private final UserService userService;

    public AdminUserEditController(UserService userService) {
        this.userService = userService;
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
    public String showUserEditForm(@PathVariable long id, @ModelAttribute("user") UserSettingsDto user) {
        Optional<User> userData = userService.findUserById(id);
        if (userData.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            setUserInitialData(userData.get(), user);
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
                           BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return showUserEditForm(id, user);
        }
        try {
            userService.updateUser(user);
            redirectAttributes.addFlashAttribute("message",
                    "Zmiana zakończyła się sukcesem");
        } catch (EmailAlreadyInUseException e) {
            bindingResult.rejectValue("email", "error.email", e.getMessage());
            return showUserEditForm(id, user);
        } catch (PeselAlreadyInUseException e) {
            bindingResult.rejectValue("pesel", "error.pesel", e.getMessage());
            return showUserEditForm(id, user);
        } catch (UserEnabledException e) {
            bindingResult.rejectValue("enabled", "error.user.enabled", e.getMessage());
            return showUserEditForm(id, user);
        }
        return "redirect:/admin/users/edit/" + id;
    }
}
