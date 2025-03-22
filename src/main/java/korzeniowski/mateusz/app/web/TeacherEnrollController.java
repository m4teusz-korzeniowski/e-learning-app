package korzeniowski.mateusz.app.web;

import jakarta.servlet.http.HttpSession;
import korzeniowski.mateusz.app.service.CourseService;
import korzeniowski.mateusz.app.model.course.dto.TeacherCourseEnrollDto;
import korzeniowski.mateusz.app.service.EnrollmentService;
import korzeniowski.mateusz.app.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.NoSuchElementException;

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

    @GetMapping("/teacher/enroll")
    public String enrollUserForm(@ModelAttribute("enroll") TeacherCourseEnrollDto enroll,
                                 Principal principal, HttpSession session) {
        if (session.getAttribute("userInfo") == null) {
            userService.addUserInfoToSession(principal.getName(), session);
        }
        return "teacher-enroll";
    }

    @PostMapping("/teacher/enroll")
    public String enrollUser(@ModelAttribute("enroll") TeacherCourseEnrollDto enroll,
                             BindingResult bindingResult, HttpSession session, Principal principal,
                             RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()) {
            return enrollUserForm(enroll, principal, session);
        }
        try {
            Long userId = userService.findUserIdByEmail(enroll.getUserEmail());
            Long courseId = courseService.findCourseIdByName(enroll.getCourseName());
            enrollmentService.enrollUserToCourse(userId,courseId);
            redirectAttributes.addFlashAttribute("message", "You have successfully enrolled!");
        }catch (NoSuchElementException e) {
            bindingResult.rejectValue("courseName", "error.courseName", e.getMessage());
            return enrollUserForm(enroll, principal, session);
        }catch (UsernameNotFoundException e) {
            bindingResult.rejectValue("userEmail", "error.userEmail",e.getMessage());
            return enrollUserForm(enroll, principal, session);
        }catch (DataIntegrityViolationException e){
            bindingResult.rejectValue("userEmail", "error.userEmail",
                    "Użytkownik jest już zapisany na dany kurs!!!");
            return enrollUserForm(enroll, principal, session);
        }
        return "redirect:/teacher";
    }

}
