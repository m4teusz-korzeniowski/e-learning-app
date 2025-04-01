package korzeniowski.mateusz.app.service;

import korzeniowski.mateusz.app.exceptions.GroupNameAlreadyExistsException;
import korzeniowski.mateusz.app.exceptions.NoSuchGroup;
import korzeniowski.mateusz.app.model.user.Group;
import korzeniowski.mateusz.app.model.user.dto.GroupDto;
import korzeniowski.mateusz.app.repository.GroupRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class GroupService {
    private final GroupRepository groupRepository;

    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Transactional
    public void createGroup(GroupDto groupDto) {
        Group group = new Group();
        group.setName(groupDto.getName());
        groupRepository.save(group);
    }

    public List<GroupDto> findAllGroups() {
        Stream<GroupDto> groups = groupRepository.findAllBy().stream().map(GroupDto::map);
        return groups.toList();
    }

    public Page<GroupDto> findAllGroupsWithPage(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return groupRepository.findAll(pageable).map(GroupDto::map);
    }

    public Page<GroupDto> findAllGroupsWithPageAndKeyword(int pageNumber, int pageSize, String keyword) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        if (!keyword.isBlank()) {
            return groupRepository.findByNameContainsIgnoreCase(keyword, pageable).map(GroupDto::map);
        } else {
            return groupRepository.findAll(pageable).map(GroupDto::map);
        }
    }

    public void removeGroup(long id) {
        groupRepository.deleteById(id);
    }

    public boolean ifGroupExist(long id) {
        return groupRepository.existsById(id);
    }

    public Optional<GroupDto> findGroupById(long id) {
        return groupRepository.findById(id).map(GroupDto::map);
    }

    @Transactional
    public void updateGroup(GroupDto groupDto) {
        Optional<Group> group = groupRepository.findById(groupDto.getId());
        if (group.isPresent()) {
            group.get().setName(groupDto.getName());
            groupRepository.save(group.get());
        } else {
            throw new NoSuchElementException("Nie znaleziono grupy");
        }
    }
}
