package korzeniowski.mateusz.app.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import korzeniowski.mateusz.app.model.user.dto.UserSessionDto;
import korzeniowski.mateusz.app.service.CourseService;
import korzeniowski.mateusz.app.model.course.dto.CourseCreationDto;
import korzeniowski.mateusz.app.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String courseCreation(@ModelAttribute("course") CourseCreationDto course,
                                 Principal principal, HttpSession session) {
        if (session.getAttribute("userInfo") == null) {
            userService.addUserInfoToSession(principal.getName(), session);
        }
        return "course-creation-form";
    }

    @PostMapping("/teacher/course/create")
    public String createCourse(@Valid @ModelAttribute("course") CourseCreationDto course, BindingResult bindingResult
            , Principal principal, HttpSession session, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return courseCreation(course, principal, session);
        }
        //Long creatorId = userService.findIdOfAuthenticatedUser(principal.getName());\
        try {
            if (session.getAttribute("userInfo") == null) {
                userService.addUserInfoToSession(principal.getName(), session);
            }
            UserSessionDto user = (UserSessionDto) session.getAttribute("userInfo");
            course.setCreatorId(user.getId());
            courseService.createCourse(course);
        } catch (DataIntegrityViolationException e) {
            bindingResult.rejectValue("name", "error.name", "Kurs już istnieje!");
            return courseCreation(course, principal, session);
        }
        redirectAttributes.addFlashAttribute(
                "message",
                String.format("Utworzono kurs: %s", course.getName())
                );
        //return courseConfirmation(course, principal, session);
        return "redirect:/teacher/course/confirmation";
    }

    @GetMapping("/teacher/course/confirmation")
    public String courseConfirmation(@ModelAttribute("message") String message,
                                     Principal principal, HttpSession session) {
        if (session.getAttribute("userInfo") == null) {
            userService.addUserInfoToSession(principal.getName(), session);
        }
        return "course-creation-confirmation";
    }

}
