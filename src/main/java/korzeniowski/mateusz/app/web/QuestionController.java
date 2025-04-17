package korzeniowski.mateusz.app.web;

import jakarta.servlet.http.HttpSession;
import korzeniowski.mateusz.app.model.course.test.dto.QuestionDisplayDto;
import korzeniowski.mateusz.app.model.course.test.dto.QuestionEditDto;
import korzeniowski.mateusz.app.model.user.dto.UserSessionDto;
import korzeniowski.mateusz.app.service.AccessService;
import korzeniowski.mateusz.app.service.AnswerService;
import korzeniowski.mateusz.app.service.QuestionService;
import korzeniowski.mateusz.app.service.TestService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.NoSuchElementException;
import java.util.Optional;

@Controller
public class QuestionController {

    private final QuestionService questionService;
    private final static int PAGE_SIZE = 20;
    private final static int MAX_NUMBER_OF_ANSWERS = 10;
    private final static int MIN_NUMBER_OF_ANSWERS = 2;
    private final AccessService accessService;
    private final TestService testService;
    private final AnswerService answerService;

    public QuestionController(QuestionService questionService, AccessService accessService, TestService testService, AnswerService answerService) {
        this.questionService = questionService;
        this.accessService = accessService;
        this.testService = testService;
        this.answerService = answerService;
    }

    @GetMapping("/teacher/test/{testId}/questions")
    public String showQuestions(@PathVariable long testId,
                                @RequestParam(value = "keyword", required = false) String keyword,
                                @RequestParam(value = "page", required = false) Integer currentPage,
                                Model model, HttpSession session) {
        try {
            UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
            if (accessService.hasLoggedInTeacherAccessToTheTest(testId, userInfo.getId())) {
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
            long courseId = testService.findCourseIdFromTest(testId);
            model.addAttribute("courseId", courseId);
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

    @GetMapping("/teacher/test/{testId}/questions/remove/{questionId}")
    public String removeQuestion(@PathVariable long testId
            , @PathVariable long questionId, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
            if (accessService.hasLoggedInTeacherAccessToQuestion(questionId, userInfo.getId())) {
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
            return "redirect:/teacher/test/" + testId + "/questions";
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/teacher/questions/edit/{questionId}")
    public String showEditableQuestion(@PathVariable long questionId, HttpSession session, Model model) {
        Optional<QuestionEditDto> foundQuestion = questionService.findQuestionById(questionId);
        foundQuestion.ifPresent(question -> {
            model.addAttribute("question", question);
            try {
                UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
                if (accessService.hasLoggedInTeacherAccessToQuestion(questionId, userInfo.getId())) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN);
                }
            } catch (NoSuchElementException e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        });
        foundQuestion.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("maxNumberOfAnswers", MAX_NUMBER_OF_ANSWERS);
        model.addAttribute("minNumberOfAnswers", MIN_NUMBER_OF_ANSWERS);
        return "question-edit";
    }

    @PostMapping("/teacher/questions/edit/{questionId}")
    public String editQuestion(@PathVariable long questionId, HttpSession session, Model model,
                               @ModelAttribute QuestionEditDto questionEditDto) {

        return "question-edit";
    }

    @GetMapping("teacher/question/{questionId}/add-answer")
    public String addAnswer(@PathVariable long questionId, HttpSession session,
                            RedirectAttributes redirectAttributes) {
        try {
            UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
            if (accessService.hasLoggedInTeacherAccessToQuestion(questionId, userInfo.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            if (questionService.maximumNumberOfQuestionReached(MAX_NUMBER_OF_ANSWERS, questionId)) {
                String message = String.format("*ilość odpowiedzi nie może przekraczać %s!",
                        MAX_NUMBER_OF_ANSWERS);
                redirectAttributes.addFlashAttribute("message", message);
            } else {
                answerService.createAnswer(questionId);
            }
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "redirect:/teacher/questions/edit/" + questionId;
    }

    @GetMapping("teacher/answer/{answerId}/remove-answer")
    public String removeAnswer(@PathVariable long answerId, HttpSession session,
                               RedirectAttributes redirectAttributes) {
        long questionId;
        try {
            UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
            if (accessService.hasLoggedInTeacherAccessToAnswer(answerId, userInfo.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            if (answerService.answerExist(answerId)) {
                questionId = answerService.findQuestionId(answerId);
                if(questionService.minimumNumberOfQuestionReached(MIN_NUMBER_OF_ANSWERS, questionId)) {
                    String message = String.format("*ilość odpowiedzi musi wynosić co najmniej %s!",
                            MIN_NUMBER_OF_ANSWERS);
                    redirectAttributes.addFlashAttribute("message", message);
                }else{
                    answerService.deleteAnswer(answerId);
                }
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }

        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "redirect:/teacher/questions/edit/" + questionId;
    }
}
