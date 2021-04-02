package com.example.accessingdatamysql.service;

import com.example.accessingdatamysql.entity.User;
import com.example.accessingdatamysql.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    public String register(User user) {
        repository.save(user);
        return "Hi " + user.getName() + " your Registration process successfully completed";
    }

    public List<User> findAllUsers() {
        return repository.findAll();
    }

    public List<User> findUser(String email) {
        return repository.findByEmail(email);
    }

    public List<User> cancelRegistration(int id) {
        repository.deleteById(id);
        return repository.findAll();
    }

}
