package korzeniowski.mateusz.app.web;

import jakarta.servlet.http.HttpSession;
import korzeniowski.mateusz.app.model.course.dto.CourseDisplayDto;
import korzeniowski.mateusz.app.service.CourseService;
import korzeniowski.mateusz.app.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

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

    @GetMapping("/course/{courseId}")
    public String showCourse(@PathVariable long courseId, Model model,
                             Principal principal, HttpSession session) {
        Long userId = userService.findIdOfAuthenticatedUser(principal.getName());
        Optional<CourseDisplayDto> courseById = courseService.findCourseById(courseId);
        if (session.getAttribute("userInfo") == null) {
            userService.addUserInfoToSession(principal.getName(), session);
        }
        if (courseById.isPresent()) {
            if (userService.ifUserHasAccessToCourse(userId, courseId)) {
                model.addAttribute("course", courseById.get());
                return "course";
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}
