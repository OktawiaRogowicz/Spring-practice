package com.example.accessingdatamysql.controller;

import com.example.accessingdatamysql.entity.User;
import com.example.accessingdatamysql.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService service;

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
    public List<User> findUser(@PathVariable String email) {
        return service.findUser(email);
    }

    @DeleteMapping("/{id}")
    public List<User> cancelRegistration(@PathVariable int id) {
        service.cancelRegistration(id);
        return service.findAllUsers();
    }

}
