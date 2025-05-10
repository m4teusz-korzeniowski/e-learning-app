package korzeniowski.mateusz.app.web;

import jakarta.servlet.http.HttpSession;
import korzeniowski.mateusz.app.exceptions.AttemptInProgressException;
import korzeniowski.mateusz.app.model.course.dto.CourseNameDto;
import korzeniowski.mateusz.app.model.user.dto.UserSessionDto;
import korzeniowski.mateusz.app.service.CourseService;
import korzeniowski.mateusz.app.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
public class AdminCourseController {

    private final static int PAGE_SIZE = 10;
    private final UserService userService;
    private final CourseService courseService;

    public AdminCourseController(UserService userService, CourseService courseService) {
        this.userService = userService;
        this.courseService = courseService;
    }

    @GetMapping("/admin/courses")
    public String showCourses(Model model, @RequestParam(value = "keyword", required = false) String keyword,
                              @RequestParam(value = "page", required = false) Integer currentPage) {
        Page<CourseNameDto> page;
        if (currentPage != null) {
            if (currentPage < 0) {
                currentPage = 0;
            }
            if (keyword != null) {
                page = courseService.findAllCoursesPageWithKeyword(keyword, currentPage, PAGE_SIZE);
            } else {
                page = courseService.findAllCoursesPage(currentPage, PAGE_SIZE);
            }
        } else {
            if (keyword != null) {
                page = courseService.findAllCoursesPageWithKeyword(keyword, 0, PAGE_SIZE);
            } else {
                page = courseService.findAllCoursesPage(0, PAGE_SIZE);
            }
        }


        List<CourseNameDto> courses = page.getContent();
        for (CourseNameDto course : courses) {
            Long creatorId = course.getCreatorId();
            userService.findUserById(creatorId).ifPresent(
                    user -> course.setCreatorName(user.getFirstName() + " " + user.getLastName()));
        }
        model.addAttribute("courses", courses);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        return "admin-courses";
    }

    @GetMapping("/admin/courses/{courseId}/remove")
    public String deleteCourse(@PathVariable("courseId") long courseId,
                               RedirectAttributes redirectAttributes) {
        try {
            courseService.deleteCourseById(courseId);
            String success = String.format("Usunięto kurs o ID = %d", courseId);
            redirectAttributes.addFlashAttribute("success", success);
            return "redirect:/admin/courses";
        } catch (NoSuchElementException | AttemptInProgressException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/courses";
        }
    }
}
