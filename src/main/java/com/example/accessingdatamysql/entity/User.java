package com.example.accessingdatamysql.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Table(name="USER_TBL")
public class User {
    @Id
    @GeneratedValue
    private int id;
    private String name;
    private String surname;
    private String password;
    private String email;
}
