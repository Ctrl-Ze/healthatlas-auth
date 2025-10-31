package com.healthatlas.auth.exception;

public class UserAlreadyExistsException extends DomainException {
    public UserAlreadyExistsException(String user) {
        super("Username " + user + " already exists."); }
}
