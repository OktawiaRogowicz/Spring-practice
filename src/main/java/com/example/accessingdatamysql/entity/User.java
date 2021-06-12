package com.example.accessingdatamysql.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Table(name="USER_TBL")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private long id;
    private String name;
    private String surname;
    private String password;
    private String email;
    private String profileImageUrl;
    private Date lastLoginDate;
    private Date lastLoginDateDisplay;
    private Date joinDate;

    private String role; // ROLE_USER{ delete, update, create }, ROLE_ADMIN
    private String[] authorities; // delete, update, create

    private boolean isActive;
    private boolean isNotLocked;

    private long[] booksLiked;
    private long[] booksBorrowed;
    private long[] booksBorrowedAllTime;
}
