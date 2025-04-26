package korzeniowski.mateusz.app.service;

import korzeniowski.mateusz.app.model.course.module.Module;
import korzeniowski.mateusz.app.model.course.module.ModuleItem;
import korzeniowski.mateusz.app.model.course.test.Answer;
import korzeniowski.mateusz.app.model.course.test.Question;
import korzeniowski.mateusz.app.model.course.test.Test;
import korzeniowski.mateusz.app.model.user.User;
import korzeniowski.mateusz.app.model.user.UserRole;
import korzeniowski.mateusz.app.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class AccessService {
    private final TestRepository testRepository;
    private final ModuleItemRepository moduleItemRepository;
    private final ModuleRepository moduleRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;

    public AccessService(TestRepository testRepository, ModuleItemRepository moduleItemRepository, ModuleRepository moduleRepository, QuestionRepository questionRepository, AnswerRepository answerRepository, UserRepository userRepository) {
        this.testRepository = testRepository;
        this.moduleItemRepository = moduleItemRepository;
        this.moduleRepository = moduleRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.userRepository = userRepository;
    }

    public boolean hasLoggedInTeacherAccessToTheCourse(Long creatorId, Long teacherId) {
        return !creatorId.equals(teacherId);
    }

    public boolean hasLoggedInTeacherAccessToTheTest(Long testId, Long teacherId) {
        Optional<Test> test = testRepository.findById(testId);
        if (test.isPresent()) {
            Long creatorId = test.get().getModule().getCourse().getCreatorId();
            return !creatorId.equals(teacherId);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    public boolean hasLoggedInTeacherAccessToModuleItem(Long moduleItemId, Long teacherId) {
        Optional<ModuleItem> item = moduleItemRepository.findById(moduleItemId);
        if (item.isPresent()) {
            Long creatorId = item.get().getModule().getCourse().getCreatorId();
            return !creatorId.equals(teacherId);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    public boolean hasLoggedInTeacherAccessToModule(Long moduleId, Long teacherId) {
        Optional<Module> item = moduleRepository.findById(moduleId);
        if (item.isPresent()) {
            Long creatorId = item.get().getCourse().getCreatorId();
            return !creatorId.equals(teacherId);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    public boolean hasLoggedInTeacherAccessToQuestion(Long questionId, Long teacherId) {
        Optional<Question> question = questionRepository.findById(questionId);
        if (question.isPresent()) {
            Long creatorId = question.get().getTest().getModule().getCourse().getCreatorId();
            return !creatorId.equals(teacherId);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    public boolean hasLoggedInTeacherAccessToAnswer(Long answerId, Long teacherId) {
        Optional<Answer> answer = answerRepository.findById(answerId);
        if (answer.isPresent()) {
            Long creatorId = answer.get().getQuestion().getTest().getModule().getCourse().getCreatorId();
            return !creatorId.equals(teacherId);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    public boolean isUserEnabled(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return user.get().getEnabled();
        } else {
            return false;
        }
    }

    public boolean hasUserAccessToResourceFile(Long itemId, Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            UserRole role = user.get().getUserRoles().iterator().next();
            if (role.getName().equals("TEACHER")) {
                return hasLoggedInTeacherAccessToModuleItem(itemId, userId);
            } else if (role.getName().equals("STUDENT")) {
                return moduleItemRepository.hasUserAccessToResourceFile(itemId, userId);
            }
        }
        return false;
    }
}
