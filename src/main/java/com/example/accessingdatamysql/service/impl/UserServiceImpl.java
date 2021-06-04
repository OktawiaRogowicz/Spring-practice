package com.example.accessingdatamysql.service.impl;

import com.example.accessingdatamysql.controller.UserImplConstant;
import com.example.accessingdatamysql.entity.User;
import com.example.accessingdatamysql.entity.UserPrincipal;
import com.example.accessingdatamysql.enumeration.Role;
import com.example.accessingdatamysql.exception.domain.EmailExistsException;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

import static com.example.accessingdatamysql.controller.UserImplConstant.*;

@Service
@Transactional
@Qualifier("UserDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;

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
            return userPrincipal;
        }
    }

    @Autowired
    private UserRepository repository;

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
    public User register(String email) throws EmailExistsException, UserNotFoundException {
        ValidateNewEmail(StringUtils.EMPTY, email);
        User user = new User();
        user.setId(generateUserId());
        String password = generatePassword();
        String encodedPassword = encodePassword(password);
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setPassword(encodedPassword);
        user.setActive(true);
        user.setNotLocked(true);
        user.setRole(Role.ROLE_USER.name());
        user.setAuthorities(Role.ROLE_USER.getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl());
        userRepository.save(user);
        logger.info("New user password: " + password);
        return user;
    }

    private String getTemporaryProfileImageUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(USER_IAMGE_PROFILE_TEMP).toUriString();
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }

    private User ValidateNewEmail(String currentEmail, String newEmail) throws UserNotFoundException, EmailExistsException {
        User currentUser = findUserByEmail(currentEmail);
        User userByNewEmail = findUserByEmail(currentEmail);
        if(StringUtils.isNotBlank(currentEmail)) {
            if(currentUser == null) {
                throw new UserNotFoundException(NO_USER_FOUND_BY_EMAIL + currentEmail);
            }
            if(userByNewEmail != null && !(currentUser.getId().equals(userByNewEmail.getId()))) {
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

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
