package com.store.product.repository;

import com.store.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p from Product p WHERE " + "p.name LIKE CONCAT ('%',:name,'%')")
    Product searchProductByName(String name);
}
