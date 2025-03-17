package korzeniowski.mateusz.app.web;

import korzeniowski.mateusz.app.model.course.Course;
import korzeniowski.mateusz.app.model.course.CourseService;
import korzeniowski.mateusz.app.model.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.Optional;

@Controller
public class CourseController {
    private final CourseService courseService;
    private final UserService userService;

    public CourseController(CourseService courseService, UserService userService) {
        this.courseService = courseService;
        this.userService = userService;
    }

    @GetMapping("/course/{id}")
    public String showCourse(@PathVariable long id, Model model, Principal principal) {
        Long userId = userService.findIdOfAuthenticatedUser(principal.getName());
        Optional<Course> courseById = courseService.findCourseById(id);
        if(courseById.isPresent()) {
            if(userService.ifUserHasAccessToCourse(userId, id)){
                model.addAttribute("course", courseById.get());
                return "course";
            }
        }
        //do poprawy
        return "redirect:/";
    }
}
