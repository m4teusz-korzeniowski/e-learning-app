package korzeniowski.mateusz.app.web;

import korzeniowski.mateusz.app.model.course.CourseService;
import korzeniowski.mateusz.app.model.course.dto.CourseNameDto;
import korzeniowski.mateusz.app.model.course.dto.TeacherCourseDto;
import korzeniowski.mateusz.app.model.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class TeacherController {
    private final UserService userService;
    private final CourseService courseService;

    public TeacherController(UserService userService, CourseService courseService) {
        this.userService = userService;
        this.courseService = courseService;
    }

    @GetMapping("/teacher")
    public String teacherHome(Model model, Principal principal) {
        Long teacherId = userService.findIdOfAuthenticatedUser(principal.getName());
        List<TeacherCourseDto> courses = courseService.findAllCoursesByTeacherId(teacherId);
        model.addAttribute("courses", courses);
        return "teacher";
    }
}
