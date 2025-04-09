package korzeniowski.mateusz.app.web;

import korzeniowski.mateusz.app.model.user.dto.UserProfileDto;
import korzeniowski.mateusz.app.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Controller
public class UserProfileController {

    private final UserService userService;

    public UserProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/{id}")
    public String showProfile(@PathVariable long id, Model model) {
        Optional<UserProfileDto> user = userService.findUserProfileById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "profile";
    }
}
