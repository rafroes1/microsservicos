package com.store.product.controller;

import com.store.product.domain.Product;
import com.store.product.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/product")
public class ProductController extends GenericController<Product>{

    public ProductController(ProductService service) { super(service); }

    @GetMapping("/search")
    public ResponseEntity<Product> searchProductByName(String name){
        return ResponseEntity.ok(service.searchProductByName(name));
    }
}
