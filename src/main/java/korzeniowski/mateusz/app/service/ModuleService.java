package korzeniowski.mateusz.app.service;

import korzeniowski.mateusz.app.model.course.module.Module;
import korzeniowski.mateusz.app.model.course.module.ModuleItem;
import korzeniowski.mateusz.app.model.course.module.dto.ModuleDisplayDto;
import korzeniowski.mateusz.app.model.course.test.Test;
import korzeniowski.mateusz.app.repository.ModuleRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ModuleService {
    private final ModuleRepository moduleRepository;
    private final CourseService courseService;
    private final static int MAX_NAME_LENGTH = 60;

    public ModuleService(ModuleRepository moduleRepository, CourseService courseService) {
        this.moduleRepository = moduleRepository;
        this.courseService = courseService;
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
            if (moduleDisplayDto.getName().length() > MAX_NAME_LENGTH) {
                throw new DataIntegrityViolationException("*przekroczono rozmiar dla nazwy modułu!");
            }
            module.get().setName(moduleDisplayDto.getName());
            module.get().setVisible(moduleDisplayDto.getVisible());
            moduleRepository.save(module.get());
        }
    }

    @Transactional
    public void createModule(Long courseId) {
        Module module = new Module();
        module.setName("Nazwa modułu");
        module.setVisible(false);
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

    public boolean maximumNumberOfItemsReached(Long moduleId, int maxSize) {
        Optional<Module> module = moduleRepository.findById(moduleId);
        if (module.isPresent()) {
            int size = module.get().getItems().size();
            return size >= maxSize;
        } else {
            throw new NoSuchElementException("Module with id " + moduleId + " not found");
        }
    }

    @Transactional
    public void addItemToModule(Long moduleId, ModuleItem item) {
        Optional<Module> module = moduleRepository.findById(moduleId);
        if (module.isPresent()) {
            item.setModule(module.get());
            module.get().getItems().add(item);
        } else {
            throw new NoSuchElementException("Nie znaleziono modułu!");
        }
    }

    public Long findCourseIdFromModule(Long moduleId) {
        Optional<Module> module = moduleRepository.findById(moduleId);
        if (module.isPresent()) {
            return module.get().getCourse().getId();
        } else {
            throw new NoSuchElementException("Module with id " + moduleId + " not found");
        }
    }
}
