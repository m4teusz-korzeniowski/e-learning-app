package korzeniowski.mateusz.app.web;

import jakarta.servlet.http.HttpSession;
import korzeniowski.mateusz.app.model.course.test.dto.QuestionDisplayDto;
import korzeniowski.mateusz.app.model.user.dto.UserSessionDto;
import korzeniowski.mateusz.app.service.CourseService;
import korzeniowski.mateusz.app.service.QuestionService;
import korzeniowski.mateusz.app.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

@Controller
public class QuestionController {

    private final QuestionService questionService;
    private final static int PAGE_SIZE = 20;
    private final CourseService courseService;
    private final UserService userService;

    public QuestionController(QuestionService questionService, CourseService courseService, UserService userService) {
        this.questionService = questionService;
        this.courseService = courseService;
        this.userService = userService;
    }

    @GetMapping("/teacher/course/edit/{courseId}/edit-test/{testId}/questions")
    public String showQuestions(@PathVariable long courseId, @PathVariable long testId,
                                @RequestParam(value = "keyword", required = false) String keyword,
                                @RequestParam(value = "page", required = false) Integer currentPage,
                                Model model, HttpSession session) {
        try {
            UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
            long creatorId = courseService.findCreatorId(courseId);
            if (!userService.ifLoggedInTeacherIsOwnerOfTheCourse(creatorId, userInfo.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            Page<QuestionDisplayDto> page;
            if (currentPage != null) {
                if (currentPage < 0) {
                    currentPage = 0;
                }
                if (keyword != null) {
                    page = questionService.findQuestionsPageWithKeyword(currentPage, PAGE_SIZE, keyword, testId);
                } else {
                    page = questionService.findQuestionsPage(currentPage, PAGE_SIZE, testId);
                }
            } else {
                if (keyword != null) {
                    page = questionService.findQuestionsPageWithKeyword(0, PAGE_SIZE, keyword, testId);
                } else {
                    page = questionService.findQuestionsPage(0, PAGE_SIZE, testId);
                }
            }
            model.addAttribute("questions", page.getContent());
            model.addAttribute("keyword", keyword);
            model.addAttribute("currentPage", currentPage);
            model.addAttribute("totalPages", page.getTotalPages());
            model.addAttribute("totalItems", page.getTotalElements());
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "questions";
    }
}
