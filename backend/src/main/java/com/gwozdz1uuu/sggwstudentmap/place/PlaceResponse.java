package com.gwozdz1uuu.sggwstudentmap.place;

public record PlaceResponse(
        Integer id,
        String name,
        Double latitude,
        Double longitude,
        String description,
        String imageUrl,
        Double averageRating
) {
    public static PlaceResponse from(Place place, Double averageRating) {
        return new PlaceResponse(
                place.getId(),
                place.getName(),
                place.getLatitude(),
                place.getLongitude(),
                place.getDescription(),
                place.getImageUrl(),
                averageRating != null ? Math.round(averageRating * 10.0) / 10.0 : null
        );
    }
}