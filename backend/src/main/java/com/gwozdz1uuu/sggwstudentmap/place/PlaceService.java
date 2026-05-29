package com.gwozdz1uuu.sggwstudentmap.place;

import com.gwozdz1uuu.sggwstudentmap.review.ReviewRepository;
import com.gwozdz1uuu.sggwstudentmap.storage.StorageService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class PlaceService {
    private static final String GOOGLE_MAPS_DIR_BASE = "https://www.google.com/maps/dir/?api=1";

    private final PlaceRepository placeRepository;
    private final ReviewRepository reviewRepository;
    private final PlacePendingRepository placePendingRepository;
    private final StorageService storageService;

    public List<PlaceResponse> getAllPlaces() {
        return getAllPlaces(PlaceSortOption.RECENT, null, null, false, null);
    }

    public List<PlaceResponse> getAllPlaces(
            PlaceSortOption sort,
            Double minRating,
            Double maxRating,
            boolean onlyRated,
            Integer limit
    ) {
        PlaceSortOption effectiveSort = sort != null ? sort : PlaceSortOption.RECENT;

        Stream<PlaceResponse> stream = placeRepository.findAll().stream()
                .map(place -> PlaceResponse.from(
                        place,
                        reviewRepository.findAverageRatingByPlaceId(place.getId()).orElse(null)
                ));

        if (onlyRated) {
            stream = stream.filter(p -> p.averageRating() != null);
        }

        if (minRating != null) {
            double threshold = minRating;
            stream = stream.filter(p -> p.averageRating() != null && p.averageRating() >= threshold);
        }

        if (maxRating != null) {
            double ceiling = maxRating;
            stream = stream.filter(p -> p.averageRating() != null && p.averageRating() <= ceiling);
        }

        Stream<PlaceResponse> sorted = stream.sorted(effectiveSort.comparator());

        if (limit != null && limit > 0) {
            sorted = sorted.limit(limit);
        }

        return sorted.toList();
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
        Optional<Place> placeOpt = placeRepository.findById(id);
        if (placeOpt.isPresent()) {
            String imageUrl = placeOpt.get().getImageUrl();
            placeRepository.deleteById(id);
            storageService.delete(imageUrl);
            return true;
        }
        return false;
    }

    @Transactional
    public PlaceResponse uploadPlaceImage(Integer placeId, MultipartFile image) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Place not found"));

        String oldImageUrl = place.getImageUrl();
        String newImageUrl = storageService.upload(image, "places", placeId);
        place.setImageUrl(newImageUrl);
        placeRepository.save(place);

        storageService.delete(oldImageUrl);

        Double avgRating = reviewRepository.findAverageRatingByPlaceId(placeId).orElse(null);
        return PlaceResponse.from(place, avgRating);
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

    public PlacePending requestAddPlace(CreatePlaceRequest request, Integer userId) {
        PlacePending pending = new PlacePending();
        pending.setName(request.getName());
        pending.setLatitude(request.getLatitude());
        pending.setLongitude(request.getLongitude());
        pending.setDescription(request.getDescription());
        pending.setActionType(PendingActionType.ADD);
        pending.setRequestedByUserId(userId);
        // status domyślnie PENDING z encji
        return placePendingRepository.save(pending);
    }

    public PlacePending requestDeletePlace(Integer placeId, Integer userId) {
        PlacePending pending = new PlacePending();
        pending.setPlaceId(placeId);
        pending.setActionType(PendingActionType.DELETE);
        pending.setRequestedByUserId(userId);
        return placePendingRepository.save(pending);
    }

    public void requestUpdatePlace(Integer placeId, CreatePlaceRequest request, Integer userId) {
        PlacePending pending = new PlacePending();
        pending.setActionType(PendingActionType.UPDATE);
        pending.setPlaceId(placeId);
        pending.setName(request.getName());
        pending.setDescription(request.getDescription());
        pending.setLatitude(request.getLatitude());
        pending.setLongitude(request.getLongitude());
        pending.setRequestedByUserId(userId);
        pending.setStatus(PendingStatus.PENDING);
        pending.setCreatedAt(LocalDateTime.now());
        placePendingRepository.save(pending);
    }

    public void approvePending(Integer pendingId) {
        PlacePending pending = placePendingRepository.findById(pendingId)
                .orElseThrow(() -> new RuntimeException("Pending not found"));

        if (pending.getActionType() == PendingActionType.ADD) {
            Place place = new Place();
            place.setName(pending.getName());
            place.setLatitude(pending.getLatitude());
            place.setLongitude(pending.getLongitude());
            place.setDescription(pending.getDescription());
            placeRepository.save(place);
        } else if (pending.getActionType() == PendingActionType.DELETE) {
            String imageUrl = placeRepository.findById(pending.getPlaceId())
                    .map(Place::getImageUrl).orElse(null);
            placeRepository.deleteById(pending.getPlaceId());
            storageService.delete(imageUrl);
        } else if (pending.getActionType() == PendingActionType.UPDATE) {
            placeRepository.findById(pending.getPlaceId()).ifPresent(place -> {
                place.setName(pending.getName());
                place.setDescription(pending.getDescription());
                place.setLatitude(pending.getLatitude());
                place.setLongitude(pending.getLongitude());
                placeRepository.save(place);
            });
        }

        pending.setStatus(PendingStatus.APPROVED);
        pending.setResolvedAt(LocalDateTime.now());
        placePendingRepository.save(pending);
    }

    public void rejectPending(Integer pendingId) {
        PlacePending pending = placePendingRepository.findById(pendingId)
                .orElseThrow(() -> new RuntimeException("Pending not found"));
        pending.setStatus(PendingStatus.REJECTED);
        pending.setResolvedAt(LocalDateTime.now());
        placePendingRepository.save(pending);
    }

    public List<PlacePending> getPendingByStatus(PendingStatus status) {
        return placePendingRepository.findByStatusOrderByCreatedAtDesc(status);
}
}
