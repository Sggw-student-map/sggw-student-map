package com.gwozdz1uuu.sggwstudentmap.place;

import java.util.Comparator;

public enum PlaceSortOption {
    RECENT,
    HIGHEST_RATED,
    LOWEST_RATED;

    public static PlaceSortOption fromString(String value) {
        if (value == null || value.isBlank()) {
            return RECENT;
        }
        try {
            return PlaceSortOption.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return RECENT;
        }
    }

    public Comparator<PlaceResponse> comparator() {
        return switch (this) {
            case HIGHEST_RATED -> Comparator
                    .comparing((PlaceResponse p) -> p.averageRating() != null ? p.averageRating() : Double.NEGATIVE_INFINITY)
                    .reversed()
                    .thenComparing(Comparator.comparing(PlaceResponse::id).reversed());
            case LOWEST_RATED -> Comparator
                    .comparing((PlaceResponse p) -> p.averageRating() != null ? p.averageRating() : Double.POSITIVE_INFINITY)
                    .thenComparing(Comparator.comparing(PlaceResponse::id).reversed());
            case RECENT -> Comparator.comparing(PlaceResponse::id).reversed();
        };
    }
}
