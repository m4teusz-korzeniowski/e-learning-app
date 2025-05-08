package korzeniowski.mateusz.app.web;

import jakarta.servlet.http.HttpSession;
import korzeniowski.mateusz.app.model.course.dto.CourseNameDto;
import korzeniowski.mateusz.app.model.course.dto.TeacherCourseDto;
import korzeniowski.mateusz.app.model.user.dto.UserSessionDto;
import korzeniowski.mateusz.app.service.CourseService;
import korzeniowski.mateusz.app.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    private final static int COURSE_PER_PAGE = 1;

    private final UserService userService;
    private final CourseService courseService;

    public HomeController(UserService userService, CourseService courseService) {
        this.userService = userService;
        this.courseService = courseService;
    }

    @GetMapping("/")
    public String showUserCourses(Model model, HttpSession session,
                                  @RequestParam(value = "keyword", required = false) String keyword,
                                  @RequestParam(value = "page", required = false) Integer currentPage) {
        UserSessionDto user = (UserSessionDto) session.getAttribute("userInfo");
        Long userId = user.getId();
        Page<CourseNameDto> page;
        if (currentPage != null) {
            if (currentPage < 0) {
                currentPage = 0;
            }
            if (keyword != null) {
                page = courseService.findUserCoursesContainKeyword(userId, currentPage, COURSE_PER_PAGE, keyword);
            } else {
                page = courseService.findUserCourses(userId, currentPage, COURSE_PER_PAGE);
            }
        } else {
            if (keyword != null) {
                page = courseService.findUserCoursesContainKeyword(userId, 0, COURSE_PER_PAGE, keyword);
            } else {
                page = courseService.findUserCourses(userId, 0, COURSE_PER_PAGE);
            }
        }
        List<CourseNameDto> courses = page.getContent();
        addCreatorNameToCourse(courses);
        model.addAttribute("courses", courses);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("courses", courses);
        return "index";
    }


    private void addCreatorNameToCourse(List<CourseNameDto> courseNameDto) {
        for (CourseNameDto course : courseNameDto) {
            String userEmailById = userService.findUserFullNameById(course.getCreatorId());
            course.setCreatorName(userEmailById);
        }
    }
}
