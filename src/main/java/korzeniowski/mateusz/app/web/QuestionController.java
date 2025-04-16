package korzeniowski.mateusz.app.web;

import jakarta.servlet.http.HttpSession;
import korzeniowski.mateusz.app.model.course.test.dto.QuestionDisplayDto;
import korzeniowski.mateusz.app.model.course.test.dto.QuestionEditDto;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.NoSuchElementException;
import java.util.Optional;

@Controller
public class QuestionController {

    private final QuestionService questionService;
    private final static int PAGE_SIZE = 20;
    private final CourseService courseService;
    private final UserService userService;
    private final static int MAX_NUMBER_OF_ANSWERS = 10;

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
            if (!userService.isLoggenInTeacherOwnerOfTheCourse(creatorId, userInfo.getId())) {
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

    @GetMapping("/teacher/course/edit/{courseId}/edit-test/{testId}/questions/remove/{questionId}")
    public String removeQuestion(@PathVariable long courseId, @PathVariable long testId
            , @PathVariable long questionId, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
            long creatorId = courseService.findCreatorId(courseId);
            if (!userService.isLoggenInTeacherOwnerOfTheCourse(creatorId, userInfo.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            if (!questionService.questionExists(questionId)) {
                redirectAttributes.addFlashAttribute("message",
                        String.format("Pytanie o ID %s nie istnieje!", questionId));
            } else {
                questionService.removeQuestion(questionId);
                redirectAttributes.addFlashAttribute("message",
                        String.format("Usunięto pytanie o ID %s", questionId));
            }
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "redirect:/teacher/course/edit/" + courseId + "/edit-test/" + testId + "/questions";
    }

    @GetMapping("/teacher/course/edit/{courseId}/edit-test/{testId}/questions/edit/{questionId}")
    public String showEditableQuestion(@PathVariable long courseId, @PathVariable long testId
            , @PathVariable long questionId, HttpSession session, Model model) {
        Optional<QuestionEditDto> foundQuestion = questionService.findQuestionById(questionId);
        foundQuestion.ifPresent(question -> {
            model.addAttribute("question", question);
            System.out.println("Typ: " + question.getType());
            try {
                UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
                long creatorId = courseService.findCreatorId(courseId);
                if (!userService.isLoggenInTeacherOwnerOfTheCourse(creatorId, userInfo.getId())) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN);
                }
            } catch (NoSuchElementException e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        });
        foundQuestion.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("maxNumberOfAnswers", MAX_NUMBER_OF_ANSWERS);
        return "question-edit";
    }

    @PostMapping("/teacher/course/edit/{courseId}/edit-test/{testId}/questions/edit/{questionId}")
    public String editQuestion(@PathVariable long courseId, @PathVariable long testId
            , @PathVariable long questionId, HttpSession session, Model model) {

        return "question-edit";
    }
}
