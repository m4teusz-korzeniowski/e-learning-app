package korzeniowski.mateusz.app.web;

import korzeniowski.mateusz.app.model.course.dto.CourseNameDto;
import korzeniowski.mateusz.app.model.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class HomeController {
    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(Model model, Principal principal) {
        Long idOfAuthenticatedUser = userService.findIdOfAuthenticatedUser(principal.getName());
        List<CourseNameDto> courses = userService.findCoursesByUserId(idOfAuthenticatedUser);
        addCreatorNameToCourse(courses);
        model.addAttribute("courses", courses);
        model.addAttribute("userName", principal.getName());
        return "index";
    }


    private void addCreatorNameToCourse(List<CourseNameDto> courseNameDto) {
        for (CourseNameDto course : courseNameDto) {
            String userEmailById = userService.findUserFullNameById(course.getCreatorId());
            course.setCreatorName(userEmailById);
        }
    }
}
