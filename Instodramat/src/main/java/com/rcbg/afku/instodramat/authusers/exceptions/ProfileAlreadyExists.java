package com.rcbg.afku.instodramat.authusers.exceptions;

public class ProfileAlreadyExists extends RuntimeException {
    public ProfileAlreadyExists(String message){
        super(message);
    }
}
