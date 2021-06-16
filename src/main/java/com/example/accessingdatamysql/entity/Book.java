package com.example.accessingdatamysql.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Table(name="BOOK_TBL")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;
    private String bookId;
    private String title;
    private String author;
    private String publishingHouse;
    private String translator;
    private String bookImageUrl;
    private int releaseYear;

}