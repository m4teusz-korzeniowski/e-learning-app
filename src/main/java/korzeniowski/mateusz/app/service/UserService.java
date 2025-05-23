package korzeniowski.mateusz.app.service;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import korzeniowski.mateusz.app.config.AppProperties;
import korzeniowski.mateusz.app.email.EmailService;
import korzeniowski.mateusz.app.exceptions.*;
import korzeniowski.mateusz.app.model.course.Course;
import korzeniowski.mateusz.app.model.course.dto.CourseNameDto;
import korzeniowski.mateusz.app.model.course.module.Module;
import korzeniowski.mateusz.app.model.token.PasswordToken;
import korzeniowski.mateusz.app.model.user.Group;
import korzeniowski.mateusz.app.model.user.User;
import korzeniowski.mateusz.app.model.user.UserCredentialsDtoMapper;
import korzeniowski.mateusz.app.model.user.UserRole;
import korzeniowski.mateusz.app.model.user.dto.*;
import korzeniowski.mateusz.app.repository.*;
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
    private final GroupRepository groupRepository;
    private final EmailService emailService;
    private final PasswordTokenService passwordTokenService;
    private final AppProperties appProperties;

    public UserService(UserRepository userRepository, UserRoleRepository userRoleRepository , GroupRepository groupRepository, EmailService emailService, PasswordTokenService passwordTokenService, AppProperties appProperties) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.groupRepository = groupRepository;
        this.emailService = emailService;
        this.passwordTokenService = passwordTokenService;
        this.appProperties = appProperties;
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
        Optional<String> fullName = userRepository.findUserFullNameById(id);
        if (fullName.isPresent()) {
            return fullName.get();
        } else {
            throw new NoSuchElementException("User not found");
        }
    }

    public List<CourseNameDto> findCoursesByUserId(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            List<Course> courses = user.get().getCourses();
            return courses.stream().map(CourseNameDto::map).toList();
        } else throw new UsernameNotFoundException(String.format("Username with ID %s not found", userId));
    }

    @Transactional
    public void sendActivationEmail(User user, String principalEmail) {
        userRepository.save(user);
        PasswordToken token = passwordTokenService.generatePasswordTokenForUser(user);
        String registerConfirmationLink =
                "<!DOCTYPE html>" +
                        "<html lang=\"pl\">" +
                        "<head>" +
                        "<meta charset=\"UTF-8\">" +
                        "<style>" +
                        "  body { font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px; }" +
                        "  .container { background: #fff; padding: 30px; border-radius: 8px; max-width: 500px; margin: auto; box-shadow: 0 2px 6px rgba(0,0,0,0.1); }" +
                        "  h2 { color: #333333; }" +
                        "  p { color: #555555; font-size: 15px; }" +
                        "  .button {" +
                        "    display: inline-block;" +
                        "    padding: 12px 24px;" +
                        "    margin-top: 20px;" +
                        "    background-color: #007bff;" +
                        "    color: white;" +
                        "    text-decoration: none;" +
                        "    border-radius: 5px;" +
                        "    font-weight: bold;" +
                        "  }" +
                        "  .button:hover { background-color: #0056b3; }" +
                        "</style>" +
                        "</head>" +
                        "<body>" +
                        "  <div class=\"container\">" +
                        "    <h2>Witaj!</h2>" +
                        "    <p>Dziękujemy za rejestrację. Aby zakończyć proces i aktywować konto, kliknij w poniższy przycisk:</p>" +
                        "    <a class=\"button\" href=\"" + appProperties.getUrl() + "/register?token=" + token.getToken() + "\">Dokończ rejestrację</a>" +
                        "    <p style=\"margin-top: 30px; font-size: 13px; color: #888888;\">Jeśli nie rejestrowałeś się w naszym serwisie, zignoruj tę wiadomość.</p>" +
                        "  </div>" +
                        "</body>" +
                        "</html>";
        try {
            emailService.sendHtmlMessage(
                    user.getEmail(),
                    "Dokończenie rejestracji",
                    registerConfirmationLink,
                    principalEmail
            );
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void registerAppUser(UserRegistrationDto userRegistrationDto, String principalEmail) {
        User user = new User();
        user.setFirstName(userRegistrationDto.getFirstName());
        user.setLastName(userRegistrationDto.getLastName());
        user.setEmail(userRegistrationDto.getEmail());
        user.setEnabled(false);
        user.setPesel(userRegistrationDto.getPesel());
        Optional<UserRole> userRole = userRoleRepository.findByName(userRegistrationDto.getRole());
        userRole.ifPresentOrElse(
                role -> user.getUserRoles().add(role),
                () -> {
                    throw new NoSuchElementException(String.format("Role %s not found", userRegistrationDto.getRole()));
                }
        );
        if (isEmailInUse(user.getEmail())) {
            throw new EmailAlreadyInUseException(String.format("*e-mail %s już istnieje", user.getEmail()));
        } else if (isPeselInUse(user.getPesel())) {
            throw new PeselAlreadyInUseException(String.format("*pesel %s już istnieje", user.getPesel()));
        } else {
            sendActivationEmail(user, principalEmail);
        }
    }

    private boolean passwordsMatch(String password, String confirmedPassword) {
        return password.equals(confirmedPassword);
    }

    @Transactional
    public void setUserPassword(User user, String password, String confirmedPassword) {
        if (!passwordsMatch(password, confirmedPassword)) {
            throw new PasswordsNotMatchException("*hasła muszą być takie same!");
        }
        user.setPassword(CreatePasswordHash.createHashBCrypt(password));
        userRepository.save(user);
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

    public Page<UserDisplayDto> findUsersPage(int pageNumber, int pageSize) {
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
                .findAllStudentsWithKeyword(keyword).stream().map(UserDisplayDto::map);
        return stream.toList();
    }

    @Transactional
    public void addUserToGroup(String userEmail, String groupName) {
        Optional<User> user = userRepository.findByEmail(userEmail);
        Optional<Group> group = groupRepository.findByName(groupName);
        if (group.isEmpty()) {
            throw new NoSuchElementException("*grupa, do której chcesz zapisać użytkowników nie istnieje!");
        }
        if (user.isEmpty()) {
            throw new NoSuchElementException("*wybierz co najmniej jednego użytkownika!");
        }
        if (!user.get().getEnabled()) {
            throw new UserDisabledException("*użytkownik, którego chcesz zapisać jest nieaktywny!");
        }
        user.get().setGroup(group.get());
        userRepository.save(user.get());

    }

    public UserSettingsDto findUserSettingsById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return user.map(UserSettingsDto::map).get();
        } else {
            throw new UsernameNotFoundException(String.format("Username with ID %s not found", userId));
        }
    }

    public List<UserDisplayDto> findTeachersWithKeyword(String keyword) {
        Stream<UserDisplayDto> stream = userRepository.findAllTeachersWithKeyword(keyword).stream().map(UserDisplayDto::map);
        return stream.toList();
    }

    public Optional<UserProfileDto> findUserProfileById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(UserProfileDto::map);
    }

    public boolean ifUserExists(Long userId) {
        return userRepository.existsById(userId);
    }

    @Transactional
    public void removeUser(Long userId) {
        userRepository.findById(userId).ifPresent(userRepository::delete);
    }


    private void updateUserData(User user, UserSettingsDto userDto) {
        if (!user.getEmail().equals(userDto.getEmail()) && isEmailInUse(userDto.getEmail())) {
            throw new EmailAlreadyInUseException("*e-mail jest już w użyciu");
        }
        if (!user.getPesel().equals(userDto.getPesel()) && isPeselInUse(userDto.getPesel())) {
            throw new PeselAlreadyInUseException("*numer PESEL jest już w użyciu");
        }
        if (userDto.getEnabled() && user.getPassword() == null) {
            throw new UserEnabledException("*nie możesz aktywować użytkownika, który nie ma ustawionego hasła!");
        }
        String role = user.getUserRoles().iterator().next().getName();
        if (role.equals("ADMIN") && !userDto.getEnabled()) {
            throw new UserEnabledException("*nie możesz dezaktywować admina!");
        }
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPesel(userDto.getPesel());
        user.setEnabled(userDto.getEnabled());
    }

    @Transactional
    public void updateUser(UserSettingsDto userDto) {
        Optional<User> user = userRepository.findById(userDto.getId());
        if (user.isPresent()) {
            updateUserData(user.get(), userDto);
            userRepository.save(user.get());
        }
    }

    public List<String> findUsersEmailsByCourseId(Long courseId) {
        return userRepository.findALlByCourseId(courseId).stream().map(User::getEmail).toList();
    }

    public List<String> findAllUserEmails() {
        return userRepository.findAll().stream().map(User::getEmail).toList();
    }

    public List<String> findUserEmailById(Long userId) {
        return userRepository.findById(userId).stream().map(User::getEmail).toList();
    }

    public List<String> findUserEmailsByGroupId(Long groupId) {
        return userRepository.findAllByGroupId(groupId).stream().map(User::getEmail).toList();
    }

    public Page<UserDisplayDto> findCourseParticipants(int pageNumber, int pageSize, long courseId) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return userRepository.findAllByCourseIdPageable(pageable, courseId).map(UserDisplayDto::map);
    }

    public Page<UserDisplayDto> findCourseParticipantsContainKeyword(int pageNumber,
                                                                     int pageSize,
                                                                     String keyword,
                                                                     long courseId) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<UserDisplayDto> users;
        if (!keyword.isBlank()) {
            users = userRepository.findAllByCourseIdPageableWithKeyword(
                            pageable, courseId, keyword)
                    .map(UserDisplayDto::map);
        } else {
            users = userRepository.findAllByCourseIdPageable(pageable, courseId).map(UserDisplayDto::map);
        }
        return users;
    }

}

