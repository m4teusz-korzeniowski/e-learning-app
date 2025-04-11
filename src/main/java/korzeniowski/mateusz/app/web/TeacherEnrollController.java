package korzeniowski.mateusz.app.web;

import jakarta.servlet.http.HttpSession;
import korzeniowski.mateusz.app.exceptions.StudentRoleMissingException;
import korzeniowski.mateusz.app.model.course.dto.CourseNameDto;
import korzeniowski.mateusz.app.model.user.dto.UserDisplayDto;
import korzeniowski.mateusz.app.model.user.dto.UserSessionDto;
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
                                 @RequestParam(name = "keyword", required = false) @ModelAttribute("keyword") String keyword,
                                 Model model, HttpSession session) {
        CourseNameDto courseNameById = courseService.findCourseNameById(id);
        if (courseNameById == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");

        if (!userService.ifLoggedInTeacherIsOwnerOfTheCourse(courseNameById.getCreatorId(), userInfo.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
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

    private boolean ifLoggedInTeacherIsOwnerOfTheCourse(Long creatorId, Long teacherId) {
        return creatorId.equals(teacherId);
    }

    @PostMapping("/teacher/course-enroll/{id}")
    public String enrollUser(@PathVariable long id, @ModelAttribute("enroll") TeacherCourseEnrollDto enroll,
                             BindingResult bindingResult, RedirectAttributes redirectAttributes,
                             @RequestParam(name = "keyword", required = false) @ModelAttribute("keyword") String keyword,
                             Model model, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return enrollUserForm(id, enroll, keyword, model, session);
        }
        try {
            StringBuilder builder = new StringBuilder
                    (String.format("Na kurs %s zapisano następujących użytkowników: ", enroll.getCourseName()));
            for (String email : enroll.getEmails()) {
                Long userId = userService.findUserIdByEmail(email);
                enrollmentService.enrollUserToCourse(userId, id);
                builder.append(email).append(", ");
            }
            if (builder.toString().equals(
                    String.format("Na kurs %s zapisano następujących użytkowników: ", enroll.getCourseName()))) {
                throw new NullPointerException();
            }
            redirectAttributes.addFlashAttribute(
                    "message", builder.substring(0, builder.length() - 2));
        } catch (UsernameNotFoundException e) {
            bindingResult.rejectValue("emails", "error.emails", e.getMessage());
            return enrollUserForm(id, enroll, keyword, model, session);
        } catch (NullPointerException e) {
            bindingResult.rejectValue("emails", "error.emails",
                    "Wybierz co najmniej jednego użytkownika!");
            return enrollUserForm(id, enroll, keyword, model, session);
        }
        return "redirect:/teacher/course-enroll/confirmation";
    }

    @GetMapping("/teacher/course-enroll/confirmation")
    public String enrollUserConfirmationForm(@ModelAttribute("message") String message) {
        return "enroll-confirmation";
    }

}
