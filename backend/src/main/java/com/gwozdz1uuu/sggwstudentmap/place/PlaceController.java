package com.gwozdz1uuu.sggwstudentmap.place;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.gwozdz1uuu.sggwstudentmap.auth.AuthService;
import com.gwozdz1uuu.sggwstudentmap.user.UserRepository;

@RestController
@RequestMapping("/api/places")
@AllArgsConstructor
@CrossOrigin
@Tag(name = "Miejsca", description = "Miejsca na mapie — lista, wyszukiwanie, CRUD, nawigacja")
public class PlaceController {
    private final PlaceService placeService;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<List<PlaceResponse>> getAllPlaces(
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "minRating", required = false) Double minRating,
            @RequestParam(name = "onlyRated", required = false, defaultValue = "false") boolean onlyRated,
            @RequestParam(name = "limit", required = false) Integer limit
    ) {
        Double safeMinRating = (minRating != null && minRating > 0) ? minRating : null;
        Integer safeLimit = (limit != null && limit > 0) ? limit : null;
        PlaceSortOption sortOption = PlaceSortOption.fromString(sort);
        return ResponseEntity.ok(placeService.getAllPlaces(sortOption, safeMinRating, onlyRated, safeLimit));
    }

    @GetMapping("/search")
    public ResponseEntity<List<PlaceResponse>> searchPlaces(@RequestParam("q") String query) {
        return ResponseEntity.ok(placeService.searchPlaces(query));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Place> getPlaceById(@PathVariable Integer id) {
        return placeService.getPlaceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createPlace(
            @Valid @RequestBody CreatePlaceRequest request,
            Authentication auth) {
        var user = authService.getCurrentUser();
        placeService.requestAddPlace(request, user.getId());
        return ResponseEntity.accepted()
                .body(Map.of("message", "Zgłoszenie dodane do zatwierdzenia"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Place> updatePlace(@PathVariable Integer id,
                                             @Valid @RequestBody CreatePlaceRequest request) {
        return placeService.updatePlace(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlace(
            @PathVariable Integer id,
            Authentication auth) {
        var user = authService.getCurrentUser();
        placeService.requestDeletePlace(id, user.getId());
        return ResponseEntity.accepted()
                .body(Map.of("message", "Zgłoszenie usunięcia dodane do zatwierdzenia"));
    }

    @GetMapping("/{id}/navigation")
    public ResponseEntity<NavigationResponse> getNavigation(@PathVariable Integer id) {
        return placeService.getNavigation(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<PlacePending>> getPending() {
        return ResponseEntity.ok(placeService.getPendingByStatus(PendingStatus.PENDING));
    }

    @PostMapping("/pending/{id}/approve")
    public ResponseEntity<Void> approve(@PathVariable Integer id) {
        placeService.approvePending(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/pending/{id}/reject")
    public ResponseEntity<Void> reject(@PathVariable Integer id) {
        placeService.rejectPending(id);
        return ResponseEntity.noContent().build();
    }
}