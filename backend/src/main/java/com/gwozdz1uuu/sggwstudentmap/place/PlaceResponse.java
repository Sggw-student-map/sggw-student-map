package com.gwozdz1uuu.sggwstudentmap.place;

public record PlaceResponse(
        Integer id,
        String name,
        Double latitude,
        Double longitude
) {
    public static PlaceResponse from(Place place) {
        return new PlaceResponse(
                place.getId(),
                place.getName(),
                place.getLatitude(),
                place.getLongitude()
        );
    }
}