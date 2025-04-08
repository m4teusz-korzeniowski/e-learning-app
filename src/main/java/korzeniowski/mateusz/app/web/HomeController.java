package korzeniowski.mateusz.app.web;

import jakarta.servlet.http.HttpSession;
import korzeniowski.mateusz.app.model.course.dto.CourseNameDto;
import korzeniowski.mateusz.app.model.user.dto.UserSessionDto;
import korzeniowski.mateusz.app.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        UserSessionDto user = (UserSessionDto) session.getAttribute("userInfo");
        Long idOfAuthenticatedUser = user.getId();
        List<CourseNameDto> courses = userService.findCoursesByUserId(idOfAuthenticatedUser);
        addCreatorNameToCourse(courses);
        model.addAttribute("courses", courses);
        return "index";
    }


    private void addCreatorNameToCourse(List<CourseNameDto> courseNameDto) {
        for (CourseNameDto course : courseNameDto) {
            String userEmailById = userService.findUserFullNameById(course.getCreatorId());
            course.setCreatorName(userEmailById);
        }
    }
}
