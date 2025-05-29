package com.cudev.demo_auth.controller.e_com.product;

import com.cudev.demo_auth.model.ReponseObject;
import com.cudev.demo_auth.service.e_com.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    ProductService productService;

    @GetMapping("/get-all-product")
    public ResponseEntity<ReponseObject> getAllProduct(@RequestParam Map<String, Object> params) {
        return ResponseEntity.ok(productService.getAllProduct(params));
    }

    @GetMapping("/get-detail-product")
    public ResponseEntity<ReponseObject> getProductById(@RequestParam int id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
}
