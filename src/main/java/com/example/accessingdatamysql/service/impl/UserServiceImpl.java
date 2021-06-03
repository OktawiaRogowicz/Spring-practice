package com.example.accessingdatamysql.service.impl;

import com.example.accessingdatamysql.entity.User;
import com.example.accessingdatamysql.entity.UserPrincipal;
import com.example.accessingdatamysql.repository.UserRepository;
import com.example.accessingdatamysql.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@Transactional
@Qualifier("UserDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            logger.error("User not found by username/email: " + email);
            throw new UsernameNotFoundException("User not found by username/email: " + email);
        } else {
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            logger.info("Returning found user by email/username: " + email);
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
}
