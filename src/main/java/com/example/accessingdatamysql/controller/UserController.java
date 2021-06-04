package com.example.accessingdatamysql.controller;

import com.example.accessingdatamysql.constant.SecurityConstant;
import com.example.accessingdatamysql.entity.User;
import com.example.accessingdatamysql.entity.UserPrincipal;
import com.example.accessingdatamysql.exception.domain.EmailExistsException;
import com.example.accessingdatamysql.exception.domain.ExceptionHandling;
import com.example.accessingdatamysql.exception.domain.UserNotFoundException;
import com.example.accessingdatamysql.service.UserService;
import com.example.accessingdatamysql.service.impl.UserServiceImpl;
import com.example.accessingdatamysql.utility.JWTTokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.core.userdetails.UserDetailsResourceFactoryBean;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = {"/", "/users"})
public class UserController extends ExceptionHandling {

    private UserService userService;
    private AuthenticationManager authenticationManager;
    private JWTTokenProvider jwtTokenProvider;

    @Autowired
    public UserController(UserService userService, AuthenticationManager authenticationManager, JWTTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) {
        authenticate(user.getEmail(), user.getPassword());
        User loginUser = userService.findUserByEmail(user.getEmail());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        return new ResponseEntity<>(loginUser, jwtHeader, HttpStatus.OK);
    }

    @GetMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) throws UserNotFoundException, EmailExistsException {
        User newUser = userService.register(user.getEmail());
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

    private HttpHeaders getJwtHeader(UserPrincipal user){
        HttpHeaders headers = new HttpHeaders();
        headers.add(SecurityConstant.JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(user));
        return headers;
    }

    private void authenticate(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
    }
/*
    @Autowired
    private UserServiceImpl service;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        service.register(user);
        return "Hi " + user.getName() + " your Registration process successfully completed";
    }

    @GetMapping("")
    public List<User> findAllUsers() {
        return service.findAllUsers();
    }

    @GetMapping("/{email}")
    public User findUser(@PathVariable String email) {
        return service.findUser(email);
    }

    @DeleteMapping("/{id}")
    public List<User> cancelRegistration(@PathVariable Long id) {
        service.cancelRegistration(id);
        return service.findAllUsers();
    }
*/
}
