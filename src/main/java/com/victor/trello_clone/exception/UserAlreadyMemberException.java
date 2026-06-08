package com.victor.trello_clone.exception;

public class UserAlreadyMemberException extends RuntimeException {

    public UserAlreadyMemberException() {
        super("User is already a member of this workspace");
    }

    public UserAlreadyMemberException(String message) {
        super(message);
    }
}