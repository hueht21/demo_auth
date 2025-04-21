package com.cudev.demo_auth.ex;

public class NotFoundException extends  RuntimeException {

    public NotFoundException(String msg) {
        super(msg);
    }
}
