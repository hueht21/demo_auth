package com.cudev.demo_auth.controller.e_com.cart;

import com.cudev.demo_auth.model.ReponseObject;
import com.cudev.demo_auth.service.e_com.cart.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    CartService cartService;

    @PostMapping("/insert-cart")
    public ResponseEntity<ReponseObject> insertCart(@RequestBody Map<String, Object> params) {
        return ResponseEntity.ok(cartService.insertProduct(params));
    }

    @GetMapping("/getListCartUser")
    public ResponseEntity<ReponseObject> getListCartUser(@RequestParam Long userId) {
        return ResponseEntity.ok(cartService.getListCartUser(userId));
    }

    @PutMapping("/updateCartNumber")
    public ResponseEntity<ReponseObject> updateCartNumber(@RequestParam Map<String, Object> params) {
        return ResponseEntity.ok(cartService.updateCartNumber(params));
    }


}
