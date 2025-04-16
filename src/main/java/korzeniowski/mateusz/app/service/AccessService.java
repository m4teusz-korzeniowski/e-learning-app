package korzeniowski.mateusz.app.service;

import jakarta.servlet.http.HttpSession;
import korzeniowski.mateusz.app.model.course.module.Module;
import korzeniowski.mateusz.app.model.course.module.ModuleItem;
import korzeniowski.mateusz.app.model.course.test.Question;
import korzeniowski.mateusz.app.model.course.test.Test;
import korzeniowski.mateusz.app.model.user.dto.UserSessionDto;
import korzeniowski.mateusz.app.repository.ModuleItemRepository;
import korzeniowski.mateusz.app.repository.ModuleRepository;
import korzeniowski.mateusz.app.repository.QuestionRepository;
import korzeniowski.mateusz.app.repository.TestRepository;
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

    public AccessService(TestRepository testRepository, ModuleItemRepository moduleItemRepository, ModuleRepository moduleRepository, QuestionRepository questionRepository) {
        this.testRepository = testRepository;
        this.moduleItemRepository = moduleItemRepository;
        this.moduleRepository = moduleRepository;
        this.questionRepository = questionRepository;
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
        if(question.isPresent()) {
            Long creatorId = question.get().getTest().getModule().getCourse().getCreatorId();
            return !creatorId.equals(teacherId);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}
