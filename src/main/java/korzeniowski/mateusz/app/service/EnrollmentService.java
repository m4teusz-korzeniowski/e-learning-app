package korzeniowski.mateusz.app.service;

import korzeniowski.mateusz.app.exceptions.StudentRoleMissingException;
import korzeniowski.mateusz.app.model.enroll.Enrollment;
import korzeniowski.mateusz.app.model.user.User;
import korzeniowski.mateusz.app.model.user.UserRole;
import korzeniowski.mateusz.app.model.user.dto.UserDisplayDto;
import korzeniowski.mateusz.app.repository.EnrollmentRepository;
import korzeniowski.mateusz.app.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


@Service
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository, UserService userService, UserRepository userRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Transactional
    public void enrollUserToCourse(Long userId, Long courseId) {
        Enrollment enrollment = new Enrollment();
        enrollment.setUser_id(userId);
        enrollment.setCourse_id(courseId);
        enrollmentRepository.save(enrollment);
    }
    
    public List<UserDisplayDto> findUsersNotEnrolledForCourse(Long courseId, String keyword) {
        Stream<UserDisplayDto> stream = userRepository.findAllByKeyword(keyword).stream().map(UserDisplayDto::map);
        List<UserDisplayDto> users = new java.util.ArrayList<>(stream.toList());
        users.removeIf(user -> checkIfUserIsEnrolledForCourse(user.getId(), courseId));
        return users;
    }
    
    private boolean checkIfUserIsEnrolledForCourse(Long userId, Long courseId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return enrollmentRepository.findByUserIdAndCourseId(userId, courseId).isPresent();
        }
        return false;
    }
}
