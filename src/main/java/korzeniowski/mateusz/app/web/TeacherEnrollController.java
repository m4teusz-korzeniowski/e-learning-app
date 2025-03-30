package korzeniowski.mateusz.app.web;

import jakarta.servlet.http.HttpSession;
import korzeniowski.mateusz.app.exceptions.StudentRoleMissingException;
import korzeniowski.mateusz.app.model.course.dto.CourseNameDto;
import korzeniowski.mateusz.app.model.user.dto.UserDisplayDto;
import korzeniowski.mateusz.app.model.user.dto.UserEmailsDto;
import korzeniowski.mateusz.app.service.CourseService;
import korzeniowski.mateusz.app.model.course.dto.TeacherCourseEnrollDto;
import korzeniowski.mateusz.app.service.EnrollmentService;
import korzeniowski.mateusz.app.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class TeacherEnrollController {
    private final EnrollmentService enrollmentService;
    private final CourseService courseService;
    private final UserService userService;

    public TeacherEnrollController(EnrollmentService enrollmentService, CourseService courseService, UserService userService) {
        this.enrollmentService = enrollmentService;
        this.courseService = courseService;
        this.userService = userService;
    }

    @GetMapping("/teacher/course-enroll/{id}")
    public String enrollUserForm(@PathVariable long id, @ModelAttribute("enroll") TeacherCourseEnrollDto enroll,
                                 Principal principal, HttpSession session,
                                 @RequestParam(name = "keyword", required = false) @ModelAttribute("keyword") String keyword,
                                 Model model) {
        if (session.getAttribute("userInfo") == null) {
            userService.addUserInfoToSession(principal.getName(), session);
        }
        CourseNameDto courseNameById = courseService.findCourseNameById(id);
        if (courseNameById == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        List<UserDisplayDto> users = new ArrayList<>();
        if (keyword != null) {
            users = enrollmentService.findUsersNotEnrolledForCourse(id, keyword);
        }
        model.addAttribute("users", users);
        enroll.setCourseName(courseNameById.getName());
        enroll.setCourseId(id);
        return "user-enroll";
    }

    @PostMapping("/teacher/course-enroll/{id}")
    public String enrollUser(@PathVariable long id, @ModelAttribute("enroll") TeacherCourseEnrollDto enroll,
                             BindingResult bindingResult, HttpSession session, Principal principal,
                             RedirectAttributes redirectAttributes,
                             @RequestParam(name = "keyword", required = false) @ModelAttribute("keyword") String keyword,
                             Model model) {
        if (bindingResult.hasErrors()) {
            return enrollUserForm(id, enroll, principal, session, keyword, model);
        }
        try {
            StringBuilder message = new StringBuilder(
                    String.format("Na kurs %s zapisano następujących użytkowników: ", enroll.getCourseName()));
            for (String email : enroll.getEmails()) {
                Long userId = userService.findUserIdByEmail(email);
                enrollmentService.enrollUserToCourse(userId, id);
                message.append("\n- ").append(email);
            }
            redirectAttributes.addFlashAttribute(
                    "message", message.toString());
        } catch (UsernameNotFoundException e) {
            bindingResult.rejectValue("emails", "error.emails", e.getMessage());
            return enrollUserForm(id, enroll, principal, session, keyword, model);
        } catch (NullPointerException e) {
            bindingResult.rejectValue("emails", "error.emails",
                    "Wybierz co najmniej jednego użytkownika!");
            return enrollUserForm(id, enroll, principal, session, keyword, model);
        }
        return "redirect:/teacher/course-enroll/confirmation";
    }

    @GetMapping("/teacher/course-enroll/confirmation")
    public String enrollUserConfirmationForm(@ModelAttribute("message") String message, Principal principal, HttpSession session) {
        if (session.getAttribute("userInfo") == null) {
            userService.addUserInfoToSession(principal.getName(), session);
        }
        return "enroll-confirmation";
    }

}
