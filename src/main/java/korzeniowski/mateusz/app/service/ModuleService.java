package korzeniowski.mateusz.app.service;

import korzeniowski.mateusz.app.model.course.module.Module;
import korzeniowski.mateusz.app.model.course.module.dto.ModuleDisplayDto;
import korzeniowski.mateusz.app.model.course.test.Test;
import korzeniowski.mateusz.app.repository.ModuleRepository;
import korzeniowski.mateusz.app.repository.TestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ModuleService {
    private final ModuleRepository moduleRepository;
    private final CourseService courseService;
    private final TestRepository testRepository;

    public ModuleService(ModuleRepository moduleRepository, CourseService courseService, TestRepository testRepository) {
        this.moduleRepository = moduleRepository;
        this.courseService = courseService;
        this.testRepository = testRepository;
    }

    public ModuleDisplayDto findModuleById(Long id) {
        Optional<Module> module = moduleRepository.findById(id);
        if (module.isPresent()) {
            return module.map(ModuleDisplayDto::map).get();
        } else {
            throw new NoSuchElementException("Module with id " + id + " not found");
        }
    }

    @Transactional
    public void updateModule(Long moduleId, ModuleDisplayDto moduleDisplayDto) {
        Optional<Module> module = moduleRepository.findById(moduleId);
        if (module.isPresent()) {
            module.get().setName(moduleDisplayDto.getName());
            moduleRepository.save(module.get());
        }
    }

    @Transactional
    public void createModule(Long courseId) {
        Module module = new Module();
        module.setName("Nazwa modułu");
        courseService.addModuleToCourse(module, courseId);
        moduleRepository.save(module);
    }

    public long findIdOfModuleCreator(Long courseId) {
        Optional<Module> module = moduleRepository.findById(courseId);
        if (module.isPresent()) {
            return module.get().getCourse().getCreatorId();
        } else {
            throw new NoSuchElementException("Module with id " + courseId + " not found");
        }
    }

    public boolean moduleExist(Long moduleId) {
        return moduleRepository.existsById(moduleId);
    }

    @Transactional
    public void deleteModule(Long id) {
        moduleRepository.deleteById(id);
    }

    public boolean maximumNumberOfTestReached(Long moduleId, int maxSize) {
        Optional<Module> module = moduleRepository.findById(moduleId);
        if (module.isPresent()) {
            int size = module.get().getTest().size();
            return size >= maxSize;
        } else {
            throw new NoSuchElementException("Module with id " + moduleId + " not found");
        }
    }

    @Transactional
    public void addTestToModule(Long moduleId, Test test) {
        Optional<Module> module = moduleRepository.findById(moduleId);
        if (module.isPresent()) {
            test.setModule(module.get());
            module.get().getTest().add(test);
        } else {
            throw new NoSuchElementException("Nie znaleziono modułu!");
        }
    }
}
