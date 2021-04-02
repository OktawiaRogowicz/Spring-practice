package com.example.accessingdatamysql.repository;

import com.example.accessingdatamysql.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    Product findByName(String name);

}
