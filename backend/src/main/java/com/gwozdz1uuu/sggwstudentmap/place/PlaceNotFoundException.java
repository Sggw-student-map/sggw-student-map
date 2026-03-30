package com.gwozdz1uuu.sggwstudentmap.place;

public class PlaceNotFoundException extends RuntimeException {

    public PlaceNotFoundException(Integer id) {
        super("Place not found with id: " + id);
    }

    public PlaceNotFoundException(String name) {
        super("Place not found with name: " + name);
    }
}