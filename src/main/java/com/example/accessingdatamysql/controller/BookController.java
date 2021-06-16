package com.example.accessingdatamysql.controller;

import com.example.accessingdatamysql.entity.Book;
import com.example.accessingdatamysql.entity.HttpResponse;
import com.example.accessingdatamysql.exception.domain.EmailExistsException;
import com.example.accessingdatamysql.exception.domain.EmailNotFoundException;
import com.example.accessingdatamysql.exception.domain.ExceptionHandling;
import com.example.accessingdatamysql.exception.domain.UserNotFoundException;
import com.example.accessingdatamysql.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.example.accessingdatamysql.constant.FileConstant.*;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping(path = {"/books"})
@CrossOrigin("http://localhost:4200")
public class BookController extends ExceptionHandling {

    public static final String BOOK_DELETED_SUCCESSFULLY = "Book deleted successfully";
    private BookService bookService;
    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping("/add")
    public ResponseEntity<Book> addNewBook(@RequestParam("bookId") String bookId,
                                           @RequestParam("title") String title,
                                           @RequestParam("author") String author,
                                           @RequestParam("publishingHouse") String publishingHouse,
                                           @RequestParam("translator") String translator,
                                           @RequestParam("relaeaseYear") int releaseYear,
                                           @RequestParam(value = "bookImage", required = false) MultipartFile profileImage) throws UserNotFoundException, IOException, EmailExistsException {
        Book newBook = bookService.addNewBook(bookId, title, author, publishingHouse, translator, profileImage, releaseYear);
        return new ResponseEntity<>(newBook, OK);
    }

    @PostMapping("/update")
    public ResponseEntity<Book> addNewBook(@RequestParam("bookId") String bookId,
                                           @RequestParam("currentTitle") String currentTitle,
                                           @RequestParam("newTitle") String newTitle,
                                           @RequestParam("author") String author,
                                           @RequestParam("publishingHouse") String publishingHouse,
                                           @RequestParam("translator") String translator,
                                           @RequestParam("relaeaseYear") int releaseYear,
                                           @RequestParam(value = "bookImage", required = false) MultipartFile profileImage) throws UserNotFoundException, IOException, EmailExistsException {
        Book updatedBook = bookService.updateBook(bookId, currentTitle, newTitle, author, publishingHouse, translator, profileImage, releaseYear);
        return new ResponseEntity<>(updatedBook, OK);
    }

    @GetMapping("/find/{bookname}")
    public ResponseEntity<Book> getBook(@PathVariable("title") String title) {
        Book book = bookService.findBookByTitle(title);
        return new ResponseEntity<>(book, OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.getBooks();
        return new ResponseEntity<>(books, OK);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('book:delete')")
    public ResponseEntity<HttpResponse> deleteBook(@PathVariable("id") long id) {
        bookService.deleteBook(id);
        return response(OK, BOOK_DELETED_SUCCESSFULLY);
    }

    @PostMapping("/updateProfileImage")
    public ResponseEntity<Book> updateProfileImage(@RequestParam("title") String title,
                                                   @RequestParam("bookImage") MultipartFile bookImage) throws EmailNotFoundException, EmailExistsException, UserNotFoundException, IOException {
        Book book = bookService.updateBookImage(title, bookImage);
        return new ResponseEntity<>(book, OK);
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        HttpResponse body = new HttpResponse(
                httpStatus.value(),
                httpStatus,
                httpStatus.getReasonPhrase().toUpperCase(),
                message.toUpperCase());

        return new ResponseEntity<>(body, httpStatus);
    }

    @GetMapping(path = "/image/{email}/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("email") String email,
                                  @PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(USER_FOLDER + email + FORWARD_SLASH + fileName));
    }

    @GetMapping(path = "/image/profile/{email}", produces = IMAGE_JPEG_VALUE)
    public byte[] getTempProfileImage(@PathVariable("email") String email) throws IOException {
        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + email);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (InputStream inputStream = url.openStream()) {
            int bytesRead;
            byte[] chunk = new byte[1024];
            while((bytesRead = inputStream.read(chunk)) > 0) {
                byteArrayOutputStream.write(chunk, 0, bytesRead);
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

}