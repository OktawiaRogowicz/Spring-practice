package com.example.accessingdatamysql.service.impl;

import com.example.accessingdatamysql.entity.Book;
import com.example.accessingdatamysql.entity.User;
import com.example.accessingdatamysql.exception.domain.EmailExistsException;
import com.example.accessingdatamysql.exception.domain.UserNotFoundException;
import com.example.accessingdatamysql.repository.BookRepository;
import com.example.accessingdatamysql.repository.UserRepository;
import com.example.accessingdatamysql.service.BookService;
import com.example.accessingdatamysql.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import static com.example.accessingdatamysql.constant.FileConstant.*;
import static com.example.accessingdatamysql.constant.UserImplConstant.EMAIL_ALREADY_EXISTS;
import static com.example.accessingdatamysql.constant.UserImplConstant.NO_USER_FOUND_BY_EMAIL;
import static org.apache.logging.log4j.util.Strings.EMPTY;

@Service
@Transactional
@Qualifier("BookDetailsService")
public class BookServiceImpl implements BookService {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private BookRepository bookRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public List<Book> getBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Book findBookByTitle(String title) {
        return bookRepository.findBookByTitle(title);
    }

    @Override
    public Book addNewBook(String bookId, String title, String author, String publishingHouse, String translator, MultipartFile bookImage, int releaseYear) throws EmailExistsException, UserNotFoundException, IOException {
        validateNewTitle(EMPTY, title);
        Book book = new Book();
        book.setId(generateBookId());
        book.setTitle(title);
        book.setAuthor(author);
        book.setPublishingHouse(publishingHouse);
        book.setTranslator(translator);
        book.setReleaseYear(releaseYear);
        bookRepository.save(book);
        saveProfileImage(book, bookImage);
        return book;
    }

    @Override
    public Book updateBook(String bookId, String currentTitle, String newTitle, String author, String publishingHouse, String translator, MultipartFile bookImage, int releaseYear) throws IOException, EmailExistsException, UserNotFoundException {
        Book currentBook = validateNewTitle(currentTitle, newTitle);

        currentBook.setTitle(newTitle);
        currentBook.setAuthor(author);
        currentBook.setPublishingHouse(publishingHouse);
        currentBook.setTranslator(translator);
        currentBook.setReleaseYear(releaseYear);
        bookRepository.save(currentBook);
        saveProfileImage(currentBook, bookImage);
        return currentBook;
    }

    @Override
    public long deleteBook(long id) {
        bookRepository.deleteById(id);
        return id;
    }

    @Override
    public Book updateBookImage(String title, MultipartFile profileImage) throws EmailExistsException, UserNotFoundException, IOException {
        Book book = validateNewTitle(title, null);
        saveProfileImage(book, profileImage);
        return book;
    }

    private void saveProfileImage(Book book, MultipartFile profileImage) throws IOException {
        if(profileImage != null) {
            Path userFolder = Paths.get(USER_FOLDER + book.getTitle()).toAbsolutePath().normalize();
            if(!Files.exists(userFolder)) {
                Files.createDirectories(userFolder);
                logger.info(DIRECTORY_CREATED);
            }
            Files.deleteIfExists(Paths.get(userFolder + book.getTitle() + DOT + JPG_EXTENSION));
            Files.copy(profileImage.getInputStream(), userFolder.resolve(book.getTitle() + DOT + JPG_EXTENSION));
            book.setBookImageUrl(setProfileImageUrl(book.getTitle()));
            bookRepository.save(book);
            logger.info(FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
        }
    }

    private String setProfileImageUrl(String email) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(USER_IMAGE_PATH + email + FORWARD_SLASH
                + email + DOT + JPG_EXTENSION).toUriString();
    }

    private Book validateNewTitle(String currentTitle, String newTitle) throws UserNotFoundException, EmailExistsException {
        Book currentBook = findBookByTitle(currentTitle);
        Book bookByNewTitle = findBookByTitle(currentTitle);
        if(StringUtils.isNotBlank(currentTitle)) {
            if(currentBook == null) {
                throw new UserNotFoundException(NO_USER_FOUND_BY_EMAIL + currentTitle);
            }
            if(bookByNewTitle != null && !(currentBook.getId() == bookByNewTitle.getId())) {
                throw new EmailExistsException(EMAIL_ALREADY_EXISTS);
            }
            return currentBook;
        } else {
            Book userByEmail = findBookByTitle(newTitle);
            if(userByEmail != null ) {
                throw new EmailExistsException(EMAIL_ALREADY_EXISTS);
            }
        }
        return null;
    }

    private long generateBookId() {
        return Long.parseLong(RandomStringUtils.randomNumeric(10));
    }

}
