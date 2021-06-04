package com.example.accessingdatamysql.service;

import com.example.accessingdatamysql.entity.User;
import com.example.accessingdatamysql.exception.domain.EmailExistsException;
import com.example.accessingdatamysql.exception.domain.UserNotFoundException;
import com.example.accessingdatamysql.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserService {
    User register(String email) throws EmailExistsException, UserNotFoundException;
    List<User> getUsers();
    User findUserByEmail(String email);
}
