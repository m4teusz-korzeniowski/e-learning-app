package korzeniowski.mateusz.app.repository;

import korzeniowski.mateusz.app.model.course.module.ModuleItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ModuleItemRepository extends CrudRepository<ModuleItem, Long> {

    @Query("select case when count(item) > 0 then false else true end" +
            " from ModuleItem item" +
            " join item.module module" +
            " join module.course course" +
            " join course.users user" +
            " where item.id = :itemId and user.id = :userId")
    Boolean hasUserAccessToResourceFile(@Param("itemId") Long itemId, @Param("userId") Long userId);
}
