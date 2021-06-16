package com.example.accessingdatamysql.repository;

import com.example.accessingdatamysql.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
    Book findBookByTitle(String title);
}