package com.cudev.demo_auth.controller;


import com.cudev.demo_auth.model.ReponseObject;
import com.cudev.demo_auth.service.KeyManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/key-manager")
public class KeyManagerController {

    @Autowired
    KeyManagerService keyManagerService;

    @GetMapping("/get-public-key")
    public ResponseEntity<ReponseObject> getPublicKey() {
        String publicKey = keyManagerService.getKeyPublic();
        if (publicKey == null) {

            return ResponseEntity.ok(new ReponseObject(false, "Public key is not available", ""));
        } else {
            return ResponseEntity.ok(new ReponseObject(true, "", publicKey));
        }
    }

}
