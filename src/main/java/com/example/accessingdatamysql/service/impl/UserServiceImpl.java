package com.example.accessingdatamysql.service.impl;

import com.example.accessingdatamysql.constant.FileConstant;
import com.example.accessingdatamysql.entity.User;
import com.example.accessingdatamysql.entity.UserPrincipal;
import com.example.accessingdatamysql.enumeration.Role;
import com.example.accessingdatamysql.exception.domain.EmailExistsException;
import com.example.accessingdatamysql.exception.domain.EmailNotFoundException;
import com.example.accessingdatamysql.exception.domain.UserNotFoundException;
import com.example.accessingdatamysql.repository.UserRepository;
import com.example.accessingdatamysql.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import static com.example.accessingdatamysql.constant.FileConstant.*;
import static com.example.accessingdatamysql.constant.UserImplConstant.*;
import static org.apache.logging.log4j.util.Strings.EMPTY;

@Service
@Transactional
@Qualifier("UserDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository repository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            logger.error(NO_USER_FOUND_BY_EMAIL + email);
            throw new UsernameNotFoundException(NO_USER_FOUND_BY_EMAIL + email);
        } else {
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            logger.info(RETURNING_FOUND_USER_BY_EMAIL + email);
            logger.info(user.getPassword());
            return userPrincipal;
        }
    }

    public String register(User user) {
        repository.save(user);
        return "Hi " + user.getName() + " your Registration process successfully completed";
    }

    public List<User> findAllUsers() {
        return repository.findAll();
    }

    public User findUser(String email) {
        return repository.findByEmail(email);
    }

    public List<User> cancelRegistration(Long id) {
        repository.deleteById(id);
        return repository.findAll();
    }

    @Override
    public User register(String name, String surname, String email) throws EmailExistsException, UserNotFoundException {
        validateNewEmail(StringUtils.EMPTY, email);
        User user = new User();
        user.setId(generateUserId());
        String password = generatePassword();
        String encodedPassword = encodePassword(password);
        user.setName(name);
        user.setSurname(surname);
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setPassword(encodedPassword);
        user.setActive(true);
        user.setNotLocked(true);
        user.setRole(Role.ROLE_USER.name());
        user.setAuthorities(Role.ROLE_USER.getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl(email));
        userRepository.save(user);
        logger.info("New user password: " + password);
        return user;
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User addNewUser(String name, String surname, String email, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws EmailExistsException, UserNotFoundException, IOException {
        validateNewEmail(EMPTY, email);
        User user = new User();
        String password = generatePassword();
        String encodedPassword = encodePassword(password);
        user.setId(generateUserId());
        user.setName(name);
        user.setSurname(surname);
        user.setJoinDate(new Date());
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setActive(isActive);
        user.setNotLocked(isNonLocked);
        user.setRole(getRoleEnumName(role).name());
        user.setProfileImageUrl(getTemporaryProfileImageUrl(email));
        user.setAuthorities(getRoleEnumName(role).getAuthorities());
        userRepository.save(user);
        saveProfileImage(user, profileImage);
        return user;
    }

    @Override
    public User updateUser(String currentEmail,
                           String newName,
                           String newSurname,
                           String newEmail,
                           String role,
                           boolean isNonLocked,
                           boolean isActive,
                           MultipartFile profileImage) throws EmailExistsException, UserNotFoundException, IOException {
        User currentUser = validateNewEmail(currentEmail, newEmail);
        currentUser.setName(newName);
        currentUser.setSurname(newSurname);
        currentUser.setEmail(newEmail);
        currentUser.setActive(isActive);
        currentUser.setNotLocked(isNonLocked);
        currentUser.setRole(getRoleEnumName(role).name());
        currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());
        userRepository.save(currentUser);
        saveProfileImage(currentUser, profileImage);
        return currentUser;
    }

    @Override
    public long deleteUser(long id) {
        userRepository.deleteById(id);
        return id;
    }

    @Override
    public String resetPassword(String email) throws EmailNotFoundException {
        User user = userRepository.findByEmail(email);
        if(user == null) {
            throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL + email);
        }
        String password = generatePassword();
        user.setPassword(encodePassword(password));
        userRepository.save(user);
        // send email to the user
        // emailService.sendNewPasswordEmail(user.getFirstName(), password, user.getEmail());
        return password;
    }

    @Override
    public User updateProfileImage(String email, MultipartFile profileImage) throws EmailExistsException, UserNotFoundException, IOException {
        User user = validateNewEmail(email, null);
        saveProfileImage(user, profileImage);
        return user;
    }

    private void saveProfileImage(User user, MultipartFile profileImage) throws IOException {
        if(profileImage != null) {
            Path userFolder = Paths.get(USER_FOLDER + user.getEmail()).toAbsolutePath().normalize();
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!" + userFolder);
            if(!Files.exists(userFolder)) {
                Files.createDirectories(userFolder);
                logger.info(DIRECTORY_CREATED);
            }
            Files.deleteIfExists(Paths.get(userFolder + user.getEmail() + DOT + JPG_EXTENSION));
            Files.copy(profileImage.getInputStream(), userFolder.resolve(user.getEmail() + DOT + JPG_EXTENSION));
            user.setProfileImageUrl(setProfileImageUrl(user.getEmail()));
            userRepository.save(user);
            logger.info(FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
        }
    }

    private String setProfileImageUrl(String email) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(USER_IMAGE_PATH + email + FORWARD_SLASH
        + email + DOT + JPG_EXTENSION).toUriString();
    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }

    private String getTemporaryProfileImageUrl(String email) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH + email).toUriString();
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private long generateUserId() {
        return Long.parseLong(RandomStringUtils.randomNumeric(10));
    }

    private User validateNewEmail(String currentEmail, String newEmail) throws UserNotFoundException, EmailExistsException {
        User currentUser = findUserByEmail(currentEmail);
        User userByNewEmail = findUserByEmail(currentEmail);
        if(StringUtils.isNotBlank(currentEmail)) {
            if(currentUser == null) {
                throw new UserNotFoundException(NO_USER_FOUND_BY_EMAIL + currentEmail);
            }
            if(userByNewEmail != null && !(currentUser.getId() == userByNewEmail.getId())) {
                throw new EmailExistsException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser;
        } else {
            User userByEmail = findUserByEmail(newEmail);
            if(userByEmail != null ) {
                throw new EmailExistsException(EMAIL_ALREADY_EXISTS);
            }
        }
        return null;
    }

}
