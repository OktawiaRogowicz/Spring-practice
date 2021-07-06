package com.example.accessingdatamysql.service;

import com.example.accessingdatamysql.entity.Book;
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

public interface BookService {

    List<Book> getBooks();
    Book findBookByTitle(String title);

    Book addNewBook(String title, String author, String publishingHouse, String translator, MultipartFile bookImage, int releaseYear) throws EmailExistsException, UserNotFoundException, IOException;
    Book updateBook(String currentTitle, String newTitle, String author, String publishingHouse, String translator, MultipartFile bookImage, int releaseYear) throws IOException, EmailExistsException, UserNotFoundException;
    long deleteBook(long id);
    Book updateBookImage(String title, MultipartFile profileImage) throws EmailExistsException, UserNotFoundException, IOException;
}
