package com.cudev.demo_auth.controller;

import com.cudev.demo_auth.model.LoginRequest;
import com.cudev.demo_auth.model.ReponseObject;
import com.cudev.demo_auth.service.AuthenticationService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthenticationService service;

    @PostMapping("/login")
    public ResponseEntity<ReponseObject> login(@RequestBody @Valid LoginRequest user) {
        try {
            ReponseObject reponseObject = service.verify(user);
            if(reponseObject.getStatus()) {
                return new ResponseEntity<ReponseObject>(
                        reponseObject,
                        HttpStatus.OK);
            }else {
                return new ResponseEntity<ReponseObject>(
                        reponseObject,
                        HttpStatus.OK);
            }
        } catch (ValidationException ex) {
            return new ResponseEntity<ReponseObject>(new ReponseObject(false, "Thất bại", ""), HttpStatus.BAD_REQUEST);
        }
    }
}
