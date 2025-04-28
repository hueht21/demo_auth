package com.cudev.demo_auth.controller;


import com.cudev.demo_auth.service.KeyManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/key-manager")
public class KeyManagerController {

    @Autowired
    KeyManagerService keyManagerService;

    @GetMapping("/get-public-key")
    public ResponseEntity<Map<String, String>> getPublicKey() {
        String publicKey = keyManagerService.getKeyPublic();
        if (publicKey == null) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Public key is not available"));
        }else {
            return ResponseEntity.ok(Map.of("publicKey", publicKey));
        }
    }

}
