package korzeniowski.mateusz.app.web;

import jakarta.servlet.http.HttpSession;
import korzeniowski.mateusz.app.model.course.dto.CourseDisplayDto;
import korzeniowski.mateusz.app.model.course.dto.CourseNameDto;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

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
                                 Principal principal, HttpSession session) {
        if (session.getAttribute("userInfo") == null) {
            userService.addUserInfoToSession(principal.getName(), session);
        }
        CourseNameDto courseNameById = courseService.findCourseNameById(id);
        enroll.setCourseName(courseNameById.getName());
        enroll.setCourseId(id);
        return "teacher-enroll";
    }

    @PostMapping("/teacher/course-enroll/{id}")
    public String enrollUser(@PathVariable long id, @ModelAttribute("enroll") TeacherCourseEnrollDto enroll,
                             BindingResult bindingResult, HttpSession session, Principal principal) {
        if (bindingResult.hasErrors()) {
            return enrollUserForm(id, enroll, principal, session);
        }
        try {
            Long userId = userService.findUserIdByEmail(enroll.getUserEmail());
            enrollmentService.enrollUserToCourse(userId, id);
        } catch (UsernameNotFoundException e) {
            bindingResult.rejectValue("userEmail", "error.userEmail", e.getMessage());
            return enrollUserForm(id, enroll, principal, session);
        } catch (DataIntegrityViolationException e) {
            bindingResult.rejectValue("userEmail", "error.userEmail",
                    "Użytkownik jest już zapisany na dany kurs!!!");
            return enrollUserForm(id, enroll, principal, session);
        }
        return "redirect:/teacher/course-enroll/" + id;
    }

}
