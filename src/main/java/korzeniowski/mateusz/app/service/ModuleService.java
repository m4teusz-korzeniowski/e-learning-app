package korzeniowski.mateusz.app.service;

import korzeniowski.mateusz.app.model.course.module.Module;
import korzeniowski.mateusz.app.model.course.module.dto.ModuleDisplayDto;
import korzeniowski.mateusz.app.repository.ModuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ModuleService {
    private final ModuleRepository moduleRepository;
    private final CourseService courseService;

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
}
