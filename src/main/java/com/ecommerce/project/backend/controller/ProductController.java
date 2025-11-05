package com.ecommerce.project.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @GetMapping
    public List<Map<String, Object>> getProducts() {
        List<Map<String, Object>> products = new ArrayList<>();

        Map<String, Object> p1 = new HashMap<>();
        p1.put("id", 1);
        p1.put("name", "게이밍 노트북");
        p1.put("price", 1500000);

        Map<String, Object> p2 = new HashMap<>();
        p2.put("id", 2);
        p2.put("name", "무선 마우스");
        p2.put("price", 39000);

        Map<String, Object> p3 = new HashMap<>();
        p3.put("id", 3);
        p3.put("name", "기계식 키보드");
        p3.put("price", 87000);

        products.add(p1);
        products.add(p2);
        products.add(p3);

        return products;
    }
}
