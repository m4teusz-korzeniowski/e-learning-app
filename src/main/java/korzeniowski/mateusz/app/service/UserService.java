package korzeniowski.mateusz.app.service;

import jakarta.servlet.http.HttpSession;
import korzeniowski.mateusz.app.exceptions.EmailAlreadyInUseException;
import korzeniowski.mateusz.app.exceptions.NoSuchGroup;
import korzeniowski.mateusz.app.exceptions.PeselAlreadyInUseException;
import korzeniowski.mateusz.app.model.course.Course;
import korzeniowski.mateusz.app.model.course.dto.CourseNameDto;
import korzeniowski.mateusz.app.model.course.module.Module;
import korzeniowski.mateusz.app.model.user.Group;
import korzeniowski.mateusz.app.model.user.User;
import korzeniowski.mateusz.app.model.user.UserCredentialsDtoMapper;
import korzeniowski.mateusz.app.model.user.UserRole;
import korzeniowski.mateusz.app.model.user.dto.*;
import korzeniowski.mateusz.app.repository.GroupRepository;
import korzeniowski.mateusz.app.repository.ResultRepository;
import korzeniowski.mateusz.app.model.course.test.Test;
import korzeniowski.mateusz.app.model.course.test.dto.ResultDto;
import korzeniowski.mateusz.app.repository.UserRepository;
import korzeniowski.mateusz.app.repository.UserRoleRepository;
import korzeniowski.mateusz.util.CreatePasswordHash;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final ResultRepository resultRepository;
    private final GroupRepository groupRepository;

    public UserService(UserRepository userRepository, UserRoleRepository userRoleRepository, ResultRepository resultRepository, GroupRepository groupRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.resultRepository = resultRepository;
        this.groupRepository = groupRepository;
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<UserCredentialsDto> findCredentialsByEmail(String email) {
        return userRepository.findByEmail(email).map(UserCredentialsDtoMapper::map);
    }

    private boolean isEmailInUse(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private boolean isPeselInUse(String pesel) {
        return userRepository.findByPesel(pesel).isPresent();
    }

    public String findUserFullNameById(Long id) {
        Optional<UserNameDto> user = userRepository.findById(id).map(UserNameDto::map);
        if (user.isPresent()) {
            return user.get().getFirstName() + " " + user.get().getLastName();
        } else {
            throw new UsernameNotFoundException(String.format("Username with ID %s not found", id));
        }
    }

    public Long findIdOfAuthenticatedUser(String name) {
        return userRepository.findByEmail(name).map(User::getId)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Username with e-mail %s not found", name)));
    }

    public List<CourseNameDto> findCoursesByUserId(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            List<Course> courses = user.get().getCourses();
            return courses.stream().map(CourseNameDto::map).toList();
        } else throw new UsernameNotFoundException(String.format("Username with ID %s not found", userId));
    }

    @Transactional
    public void registerAppUser(UserRegistrationDto userRegistrationDto) {
        User user = new User();
        user.setFirstName(userRegistrationDto.getFirstName());
        user.setLastName(userRegistrationDto.getLastName());
        user.setEmail(userRegistrationDto.getEmail());
        String password = CreatePasswordHash.createHashBCrypt(userRegistrationDto.getPassword());
        user.setPassword(password);
        user.setPesel(userRegistrationDto.getPesel());
        Optional<UserRole> userRole = userRoleRepository.findByName(userRegistrationDto.getRole());
        userRole.ifPresentOrElse(
                role -> user.getUserRoles().add(role),
                () -> {
                    throw new NoSuchElementException(String.format("Role %s not found", userRegistrationDto.getRole()));
                }
        );
        if (isEmailInUse(user.getEmail())) {
            throw new EmailAlreadyInUseException(String.format("e-mail %s już istnieje", user.getEmail()));
        } else if (isPeselInUse(user.getPesel())) {
            throw new PeselAlreadyInUseException(String.format("pesel %s już istnieje", user.getPesel()));
        } else {
            userRepository.save(user);
        }
    }

    public boolean ifUserHasAccessToCourse(Long userId, Long courseId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            List<Course> courses = user.get().getCourses();
            for (Course course : courses) {
                if (course.getId().equals(courseId)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Optional<ResultDto> findUserResultOfTest(Long userId, Long testId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            //result.ifPresent(value -> System.out.println("Wynik jest = " + value.getScore()));
            return resultRepository.findByUserId(userId, testId).map(ResultDto::map);
        }
        throw new UsernameNotFoundException(String.format("Username with ID %s not found", userId));
    }

    public boolean ifUserHasAccessToTest(Long userId, Long testId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            List<Course> courses = user.get().getCourses();
            for (Course course : courses) {
                for (Module module : course.getModules()) {
                    for (Test test : module.getTest()) {
                        if (test.getId().equals(testId)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public Long findUserIdByEmail(String email) {
        Optional<Long> userId = userRepository.findByEmail(email).map(User::getId);
        return userId.orElseThrow(() -> new UsernameNotFoundException(String.format("Nie znaleziono użytkownika z podanym adresem email - %s", email)));
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void addUserInfoToSession(String userName, HttpSession session) {
        Optional<User> user = findUserByEmail(userName);
        if (user.isPresent()) {
            UserSessionDto userSessionDto = new UserSessionDto();
            userSessionDto.setId(user.get().getId());
            userSessionDto.setFirstName(user.get().getFirstName());
            userSessionDto.setLastName(user.get().getLastName());
            session.setAttribute("userInfo", userSessionDto);
        }
    }

    public void addUserInfoToNavigationBar(String userName, Model model) {
        Optional<User> user = findUserByEmail(userName);
        if (user.isPresent()) {
            UserSessionDto userSessionDto = new UserSessionDto();
            userSessionDto.setId(user.get().getId());
            userSessionDto.setFirstName(user.get().getFirstName());
            userSessionDto.setLastName(user.get().getLastName());
            model.addAttribute("userInfo", userSessionDto);
        }
    }

    public List<UserDisplayDto> findAllUsers() {
        Stream<UserDisplayDto> users = userRepository.findAll().stream().map(UserDisplayDto::map);
        return users.toList();
    }

    public Page<UserDisplayDto> findUserPage(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return userRepository.findAll(pageable).map(UserDisplayDto::map);
    }

    public Page<UserDisplayDto> findUsersPageContainKeyword(int pageNumber, int pageSize, String keyword) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<UserDisplayDto> users;
        if (!keyword.isBlank()) {
            users = userRepository.findAllByKeywordPageable(
                            keyword, pageable)
                    .map(UserDisplayDto::map);
        } else {
            users = userRepository.findAllBy(pageable).map(UserDisplayDto::map);
        }
        return users;
    }

    public List<UserDisplayDto> findUsersWithoutGroupContainKeyword(String keyword) {
        Stream<UserDisplayDto> stream = userRepository
                .findAllStudentsWithoutGroupAndKeyword(keyword).stream().map(UserDisplayDto::map);
        return stream.toList();
    }

    @Transactional
    public void addUserToGroup(String userEmail, String groupName) {
        Optional<User> user = userRepository.findByEmail(userEmail);
        Group group = groupRepository.findByName(groupName);
        if (group == null) {
            throw new NoSuchGroup("Grupa, do której chcesz zapisać użytkowników nie istnieje!");
        }
        if (user.isPresent()) {
            user.get().setGroup(group);
            userRepository.save(user.get());
        }
    }
}

