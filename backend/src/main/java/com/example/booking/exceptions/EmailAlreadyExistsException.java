package com.example.booking.exceptions;

public class EmailAlreadyExistsException extends RuntimeException{
    public EmailAlreadyExistsException(String mail){
        super("User with mail: " + mail + " already exists!");
    }
}
