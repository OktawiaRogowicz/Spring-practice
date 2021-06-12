package com.example.accessingdatamysql.service;

import com.example.accessingdatamysql.entity.User;
import com.example.accessingdatamysql.exception.domain.EmailExistsException;
import com.example.accessingdatamysql.exception.domain.EmailNotFoundException;
import com.example.accessingdatamysql.exception.domain.UserNotFoundException;
import com.example.accessingdatamysql.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    User register(String name, String surname, String email) throws EmailExistsException, UserNotFoundException;
    List<User> getUsers();
    User findUserByEmail(String email);

    User addNewUser(String name, String surname, String email, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws EmailExistsException, IOException, UserNotFoundException;
    User updateUser(String currentEmail, String newName, String newSurname, String newEmail, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws IOException, EmailExistsException, UserNotFoundException;
    long deleteUser(long id);
    String resetPassword(String email) throws EmailNotFoundException;
    User updateProfileImage(String email, MultipartFile profileImage) throws EmailExistsException, UserNotFoundException, IOException;
}
