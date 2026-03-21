package com.gwozdz1uuu.sggwstudentmap.user;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Integer id) {
        super("User not found with id: " + id);
    }
}
