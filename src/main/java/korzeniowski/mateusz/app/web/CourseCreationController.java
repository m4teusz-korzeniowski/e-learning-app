package korzeniowski.mateusz.app.web;

import jakarta.validation.Valid;
import korzeniowski.mateusz.app.model.course.CourseService;
import korzeniowski.mateusz.app.model.course.dto.CourseCreationDto;
import korzeniowski.mateusz.app.model.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class CourseCreationController {
    private final UserService userService;
    private final CourseService courseService;

    public CourseCreationController(UserService userService, CourseService courseService) {
        this.userService = userService;
        this.courseService = courseService;
    }

    @GetMapping("/teacher/course/create")
    public String courseCreation(@ModelAttribute("course") CourseCreationDto course) {
        return "course-creation-form";
    }

    @PostMapping("/teacher/course/create")
    public String createCourse(@Valid @ModelAttribute("course") CourseCreationDto course, BindingResult bindingResult
            , Principal principal) {
        if (bindingResult.hasErrors()) {
            return courseCreation(course);
        }
        Long creatorId = userService.findIdOfAuthenticatedUser(principal.getName());
        course.setCreatorId(creatorId);
        courseService.createCourse(course);
        return "redirect:/teacher/course/confirmation";
    }

    @GetMapping("/teacher/course/confirmation")
    public String courseConfirmation() {
        return "course-creation-confirmation";
    }

}
