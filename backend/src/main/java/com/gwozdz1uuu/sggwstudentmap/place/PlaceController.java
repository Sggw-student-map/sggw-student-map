package com.gwozdz1uuu.sggwstudentmap.place;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/places")
@AllArgsConstructor
@CrossOrigin
@Tag(name = "Miejsca", description = "Miejsca na mapie — lista, wyszukiwanie, CRUD, nawigacja")
public class PlaceController {
    private final PlaceService placeService;

    @GetMapping
    public ResponseEntity<List<PlaceResponse>> getAllPlaces(
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "minRating", required = false) Double minRating,
            @RequestParam(name = "maxRating", required = false) Double maxRating,
            @RequestParam(name = "onlyRated", required = false, defaultValue = "false") boolean onlyRated,
            @RequestParam(name = "limit", required = false) Integer limit
    ) {
        Double safeMinRating = (minRating != null && minRating > 0) ? minRating : null;
        Double safeMaxRating = (maxRating != null && maxRating > 0 && maxRating < 5) ? maxRating : null;
        if (safeMinRating != null && safeMaxRating != null && safeMaxRating < safeMinRating) {
            safeMaxRating = safeMinRating;
        }
        Integer safeLimit = (limit != null && limit > 0) ? limit : null;
        PlaceSortOption sortOption = PlaceSortOption.fromString(sort);
        return ResponseEntity.ok(
                placeService.getAllPlaces(sortOption, safeMinRating, safeMaxRating, onlyRated, safeLimit)
        );
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
    public ResponseEntity<Place> createPlace(@Valid @RequestBody CreatePlaceRequest request) {
        Place created = placeService.createPlace(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Place> updatePlace(@PathVariable Integer id,
                                             @Valid @RequestBody CreatePlaceRequest request) {
        return placeService.updatePlace(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlace(@PathVariable Integer id) {
        if (placeService.deletePlace(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/navigation")
    public ResponseEntity<NavigationResponse> getNavigation(@PathVariable Integer id) {
        return placeService.getNavigation(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
