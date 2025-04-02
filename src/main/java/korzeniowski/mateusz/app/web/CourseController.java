package korzeniowski.mateusz.app.web;

import jakarta.servlet.http.HttpSession;
import korzeniowski.mateusz.app.model.course.dto.CourseDisplayDto;
import korzeniowski.mateusz.app.model.user.dto.UserSessionDto;
import korzeniowski.mateusz.app.service.CourseService;
import korzeniowski.mateusz.app.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

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
                             HttpSession session) {
        UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
        Long userId = userInfo.getId();
        Optional<CourseDisplayDto> courseById = courseService.findCourseById(courseId);
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
