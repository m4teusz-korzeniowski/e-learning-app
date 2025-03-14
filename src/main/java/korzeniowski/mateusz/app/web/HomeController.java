package korzeniowski.mateusz.app.web;

import korzeniowski.mateusz.app.model.course.CourseService;
import korzeniowski.mateusz.app.model.course.dto.CourseNameDto;
import korzeniowski.mateusz.app.model.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    private final CourseService courseService;
    private final UserService userService;

    public HomeController(CourseService courseService, UserService userService) {
        this.courseService = courseService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<CourseNameDto> courses = courseService.findAllCourses();
        addCreatorNameToCourse(courses);
        model.addAttribute("courses", courses);
        return "index";
    }

    private void addCreatorNameToCourse(List<CourseNameDto> courseNameDto) {
        for (CourseNameDto course : courseNameDto) {
            String userEmailById = userService.findUserNameById(course.getCreatorId());
            course.setCreatorName(userEmailById);
        }
    }
}
