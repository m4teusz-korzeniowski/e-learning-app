package korzeniowski.mateusz.app.model.user;

import korzeniowski.mateusz.app.exceptions.EmailAlreadyInUseException;
import korzeniowski.mateusz.app.model.course.Course;
import korzeniowski.mateusz.app.model.course.CourseRepository;
import korzeniowski.mateusz.app.model.course.dto.CourseNameDto;
import korzeniowski.mateusz.app.model.user.dto.UserCredentialsDto;
import korzeniowski.mateusz.app.model.user.dto.UserNameDto;
import korzeniowski.mateusz.app.model.user.dto.UserRegistrationDto;
import korzeniowski.mateusz.util.CreatePasswordHash;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    public UserService(UserRepository userRepository, UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
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
            System.out.printf("Email %s is already in use%n", user.getEmail());
            throw new EmailAlreadyInUseException(String.format("Email %s is already in use", user.getEmail()));
        } else {
            userRepository.save(user);
        }
    }
}
