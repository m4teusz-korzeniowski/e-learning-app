package korzeniowski.mateusz.app.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import korzeniowski.mateusz.app.model.user.dto.UserSessionDto;
import korzeniowski.mateusz.app.service.CourseService;
import korzeniowski.mateusz.app.model.course.dto.CourseCreationDto;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class CourseCreationController {
    private final CourseService courseService;

    public CourseCreationController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/teacher/course/create")
    public String courseCreation(@ModelAttribute("course") CourseCreationDto course) {
        return "course-creation-form";
    }

    @PostMapping("/teacher/course/create")
    public String createCourse(@Valid @ModelAttribute("course") CourseCreationDto course, BindingResult bindingResult
            , HttpSession session, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return courseCreation(course);
        }
        try {
            UserSessionDto user = (UserSessionDto) session.getAttribute("userInfo");
            course.setCreatorId(user.getId());
            courseService.createCourse(course);
        } catch (DataIntegrityViolationException e) {
            bindingResult.rejectValue("name", "error.name", "Kurs już istnieje!");
            return courseCreation(course);
        }
        redirectAttributes.addFlashAttribute(
                "message",
                String.format("Utworzono kurs: %s", course.getName())
        );
        return "redirect:/teacher/course/confirmation";
    }

    @GetMapping("/teacher/course/confirmation")
    public String courseConfirmation(@ModelAttribute("message") String message) {
        return "course-creation-confirmation";
    }

}
