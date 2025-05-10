package korzeniowski.mateusz.app.web;

import jakarta.validation.Valid;
import korzeniowski.mateusz.app.model.user.dto.UserDisplayDto;
import korzeniowski.mateusz.app.service.CourseService;
import korzeniowski.mateusz.app.model.course.dto.CourseCreationDto;
import korzeniowski.mateusz.app.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


@Controller
public class CourseCreationController {
    private final CourseService courseService;
    private final UserService userService;

    public CourseCreationController(CourseService courseService, UserService userService) {
        this.courseService = courseService;
        this.userService = userService;
    }

    @GetMapping("/admin/course/create")
    public String courseCreation(@ModelAttribute("course") CourseCreationDto course,
                                 @RequestParam(value = "keyword", required = false) String keyword,
                                 Model model) {
        List<UserDisplayDto> users;
        if (keyword != null && !keyword.isEmpty()) {
            users = userService.findTeachersWithKeyword(keyword);
        } else {
            users = new ArrayList<>();
        }
        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);
        return "course-creation-form";
    }

    @PostMapping("/admin/course/create")
    public String createCourse(@Valid @ModelAttribute("course") CourseCreationDto course, BindingResult bindingResult
            , RedirectAttributes redirectAttributes, Model model,
                               @ModelAttribute("keyword") String keyword) {
        if (bindingResult.hasErrors()) {
            return courseCreation(course, keyword, model);
        }
        try {
            courseService.createCourse(course);
        } catch (DataIntegrityViolationException e) {
            bindingResult.rejectValue("name", "error.name", "*kurs już istnieje!");
            return courseCreation(course, keyword, model);
        } catch (NoSuchElementException e) {
            bindingResult.rejectValue("name", "error.name", e.getMessage());
            return courseCreation(course, keyword, model);
        }
        redirectAttributes.addFlashAttribute(
                "message",
                String.format("Utworzono kurs: %s oraz przypisano do niego nauczyciela o ID = %s",
                        course.getName(), course.getCreatorId())
        );
        return "redirect:/admin/course/confirmation";
    }

    @GetMapping("/admin/course/confirmation")
    public String courseConfirmation(@ModelAttribute("message") String message) {
        return "course-creation-confirmation";
    }

}
