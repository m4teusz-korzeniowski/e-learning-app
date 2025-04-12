package korzeniowski.mateusz.app.service;

import korzeniowski.mateusz.app.model.course.module.ModuleItem;
import korzeniowski.mateusz.app.model.course.test.Test;
import korzeniowski.mateusz.app.repository.ModuleItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ModuleItemService {
    private final ModuleItemRepository moduleItemRepository;
    private final ModuleService moduleService;

    public ModuleItemService(ModuleItemRepository moduleItemRepository, ModuleService moduleService) {
        this.moduleItemRepository = moduleItemRepository;
        this.moduleService = moduleService;
    }

    @Transactional
    public void createModuleItem(Long moduleId) {
        ModuleItem moduleItem = new ModuleItem();
        moduleItem.setName("Nazwa elementu");
        moduleService.addItemToModule(moduleId, moduleItem);
        moduleItemRepository.save(moduleItem);
    }

    public boolean moduleItemExists(Long itemId) {
        return moduleItemRepository.existsById(itemId);
    }

    @Transactional
    public void deleteModuleItem(Long itemId) {
        moduleItemRepository.findById(itemId).ifPresent(moduleItemRepository::delete);
    }
}
