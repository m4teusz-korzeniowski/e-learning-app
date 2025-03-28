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
public class TeacherEditorController {
    private final UserService userService;
    private final CourseService courseService;

    public TeacherEditorController(UserService userService, CourseService courseService) {
        this.userService = userService;
        this.courseService = courseService;
    }

    @GetMapping("/teacher/course/edit/{id}")
    public String showEditableCourse(@PathVariable long id, Principal principal, HttpSession session
            , Model model) {
        if (session.getAttribute("userInfo") == null) {
            userService.addUserInfoToSession(principal.getName(), session);
        }
        Optional<CourseDisplayDto> course = courseService.findCourseById(id);
        course.ifPresent(courseDisplayDto -> model.addAttribute("course", courseDisplayDto));
        course.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return "course-editor";
    }
}
