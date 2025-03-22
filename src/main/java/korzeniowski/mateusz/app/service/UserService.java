package korzeniowski.mateusz.app.service;

import jakarta.servlet.http.HttpSession;
import korzeniowski.mateusz.app.exceptions.EmailAlreadyInUseException;
import korzeniowski.mateusz.app.model.course.Course;
import korzeniowski.mateusz.app.model.course.dto.CourseNameDto;
import korzeniowski.mateusz.app.model.course.module.Module;
import korzeniowski.mateusz.app.model.user.User;
import korzeniowski.mateusz.app.model.user.UserCredentialsDtoMapper;
import korzeniowski.mateusz.app.model.user.UserRole;
import korzeniowski.mateusz.app.repository.ResultRepository;
import korzeniowski.mateusz.app.model.course.test.Test;
import korzeniowski.mateusz.app.model.course.test.dto.ResultDto;
import korzeniowski.mateusz.app.model.user.dto.UserCredentialsDto;
import korzeniowski.mateusz.app.model.user.dto.UserNameDto;
import korzeniowski.mateusz.app.model.user.dto.UserRegistrationDto;
import korzeniowski.mateusz.app.model.user.dto.UserSessionDto;
import korzeniowski.mateusz.app.repository.UserRepository;
import korzeniowski.mateusz.app.repository.UserRoleRepository;
import korzeniowski.mateusz.util.CreatePasswordHash;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final ResultRepository resultRepository;

    public UserService(UserRepository userRepository, UserRoleRepository userRoleRepository, ResultRepository resultRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.resultRepository = resultRepository;
    }

    public Optional<User> findUserById(Long id){
        return userRepository.findById(id);
    }

    public Optional<UserCredentialsDto> findCredentialsByEmail(String email) {
        return userRepository.findByEmail(email).map(UserCredentialsDtoMapper::map);
    }

    private boolean isEmailInUse(String email) {
        return userRepository.findByEmail(email).isPresent();
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
        Optional<UserRole> userRole = userRoleRepository.findByName(userRegistrationDto.getRole());
        userRole.ifPresentOrElse(
                role -> user.getUserRoles().add(role),
                () -> {
                    throw new NoSuchElementException(String.format("Role %s not found", userRegistrationDto.getRole()));
                }
        );
        if (isEmailInUse(user.getEmail())) {
            throw new EmailAlreadyInUseException(String.format("Email %s is already in use", user.getEmail()));
        } else {
            userRepository.save(user);
        }
    }

    public boolean ifUserHasAccessToCourse(Long userId, Long courseId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            List<Course> courses = user.get().getCourses();
            for (Course course : courses) {
                if(course.getId().equals(courseId)) {
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
            return resultRepository.findByUserId(userId,testId).map(ResultDto::map);
        }
        throw new UsernameNotFoundException(String.format("Username with ID %s not found", userId));
    }

    public boolean ifUserHasAccessToTest(Long userId, Long testId){
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            List<Course> courses = user.get().getCourses();
            for (Course course : courses) {
                for (Module module : course.getModules()) {
                    for (Test test : module.getTest()) {
                        if(test.getId().equals(testId)) {
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
        return userId.orElseThrow(() -> new UsernameNotFoundException(String.format("Username with email %s not found", email)));
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

}
