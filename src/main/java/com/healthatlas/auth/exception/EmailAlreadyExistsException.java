package com.healthatlas.auth.exception;

public class EmailAlreadyExistsException extends DomainException {
    public EmailAlreadyExistsException(String email) {
        super("Email " + email + " already exists."); }
}
