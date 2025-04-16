package korzeniowski.mateusz.app.service;

import jakarta.servlet.http.HttpSession;
import korzeniowski.mateusz.app.model.user.dto.UserSessionDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AccessService {
    private final CourseService courseService;
    private final UserService userService;

    public AccessService(CourseService courseService, UserService userService) {
        this.courseService = courseService;
        this.userService = userService;
    }

    public boolean teacherHasAccessToCourse(HttpSession session, Long courseId) {
        UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
        long creatorId = courseService.findCreatorId(courseId);
        if (!userService.isLoggenInTeacherOwnerOfTheCourse(creatorId, userInfo.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return true;
    }
}
