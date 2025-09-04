package com.vpe.finalstore;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductRepository pr;

    @GetMapping
    public List<Product> getAllProducts() {
        return pr.findAll();
    }
}
