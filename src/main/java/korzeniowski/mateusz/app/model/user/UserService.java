package korzeniowski.mateusz.app.model.user;

import korzeniowski.mateusz.app.model.course.Course;
import korzeniowski.mateusz.app.model.course.dto.CourseNameDto;
import korzeniowski.mateusz.app.model.user.dto.UserCourseDto;
import korzeniowski.mateusz.app.model.user.dto.UserCredentialsDto;
import korzeniowski.mateusz.app.model.user.dto.UserNameDto;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<UserCredentialsDto> findCredentialsByEmail(String email) {
        return userRepository.findByEmail(email).map(UserCredentialsDtoMapper::map);
    }

    public String findUserNameById(Long id) {
        Optional<UserNameDto> byId = userRepository.findById(id).map(UserNameDto::map);
        if (byId.isPresent()) {
            return byId.get().getFirstName() + " " + byId.get().getLastName();
        } else {
            throw new UsernameNotFoundException(String.format("Username with id %s not found", id));
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
        }else throw new UsernameNotFoundException(String.format("Username with ID %s not found", userId));
    }
}
