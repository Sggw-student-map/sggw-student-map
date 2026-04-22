package com.gwozdz1uuu.sggwstudentmap.place;

import com.gwozdz1uuu.sggwstudentmap.review.ReviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PlaceService {
    private static final String GOOGLE_MAPS_DIR_BASE = "https://www.google.com/maps/dir/?api=1";

    private final PlaceRepository placeRepository;
    private final ReviewRepository reviewRepository;

    public List<PlaceResponse> getAllPlaces() {
        return placeRepository.findAll().stream()
                .map(place -> PlaceResponse.from(
                        place,
                        reviewRepository.findAverageRatingByPlaceId(place.getId()).orElse(null)
                ))
                .toList();
    }

    public List<PlaceResponse> searchPlaces(String query) {
        return placeRepository.findByNameContainingIgnoreCase(query).stream()
                .map(place -> PlaceResponse.from(
                        place,
                        reviewRepository.findAverageRatingByPlaceId(place.getId()).orElse(null)
                ))
                .toList();
    }

    public Optional<Place> getPlaceById(Integer id) {
        return placeRepository.findById(id);
    }

    public Place createPlace(CreatePlaceRequest request) {
        Place place = new Place();
        place.setName(request.getName());
        place.setLatitude(request.getLatitude());
        place.setLongitude(request.getLongitude());
        place.setDescription(request.getDescription());
        return placeRepository.save(place);
    }

    public Optional<Place> updatePlace(Integer id, CreatePlaceRequest request) {
        return placeRepository.findById(id).map(existing -> {
            existing.setName(request.getName());
            existing.setLatitude(request.getLatitude());
            existing.setLongitude(request.getLongitude());
            existing.setDescription(request.getDescription());
            return placeRepository.save(existing);
        });
    }

    public boolean deletePlace(Integer id) {
        if (placeRepository.existsById(id)) {
            placeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<NavigationResponse> getNavigation(Integer placeId) {
        return placeRepository.findById(placeId).map(place -> {
            String googleMapsUrl = String.format(
                    Locale.US,
                    "%s&destination=%f,%f&travelmode=walking",
                    GOOGLE_MAPS_DIR_BASE,
                    place.getLatitude(),
                    place.getLongitude()
            );

            return new NavigationResponse(
                    place.getId(),
                    place.getName(),
                    place.getLatitude(),
                    place.getLongitude(),
                    googleMapsUrl
            );
        });
    }
}
