package korzeniowski.mateusz.app.web;

import jakarta.servlet.http.HttpSession;
import korzeniowski.mateusz.app.model.course.dto.CourseNameDto;
import korzeniowski.mateusz.app.model.user.User;
import korzeniowski.mateusz.app.model.user.UserService;
import korzeniowski.mateusz.app.model.user.dto.UserSessionDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {
    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(Model model, Principal principal, HttpSession session) {
        Long idOfAuthenticatedUser = userService.findIdOfAuthenticatedUser(principal.getName());
        List<CourseNameDto> courses = userService.findCoursesByUserId(idOfAuthenticatedUser);
        addCreatorNameToCourse(courses);
        model.addAttribute("courses", courses);
        userService.addUserInfoToSession(principal.getName(), session);
        /*Optional<User> user = userService.findUserByEmail(principal.getName());
        if (user.isPresent()) {
            UserSessionDto userSessionDto = new UserSessionDto();
            userSessionDto.setId(user.get().getId());
            userSessionDto.setFirstName(user.get().getFirstName());
            userSessionDto.setLastName(user.get().getLastName());
            session.setAttribute("userInfo", userSessionDto);
        }*/
        return "index";
    }


    private void addCreatorNameToCourse(List<CourseNameDto> courseNameDto) {
        for (CourseNameDto course : courseNameDto) {
            String userEmailById = userService.findUserFullNameById(course.getCreatorId());
            course.setCreatorName(userEmailById);
        }
    }
}
