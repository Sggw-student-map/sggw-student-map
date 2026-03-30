package com.gwozdz1uuu.sggwstudentmap.place;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/pins")
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping
    public ResponseEntity<List<PlaceResponse>> getAllPlaces() {
        return ResponseEntity.ok(placeService.getAllPlaces());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaceResponse> getPlaceById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(placeService.getPlaceById(id));
        } catch (PlaceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<PlaceResponse> getPlaceByName(@PathVariable String name) {
        try {
            return ResponseEntity.ok(placeService.getPlaceByName(name));
        } catch (PlaceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<PlaceResponse>> getPlacesByPattern(@RequestParam String pattern) {
        return ResponseEntity.ok(placeService.getPlacesByPattern(pattern));
    }

    @PostMapping
    public ResponseEntity<PlaceResponse> createPlace(@RequestBody PlaceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(placeService.createPlace(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlaceResponse> updatePlace(@PathVariable Integer id,
                                                     @RequestBody PlaceRequest request) {
        try {
            return ResponseEntity.ok(placeService.updatePlace(id, request));
        } catch (PlaceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlace(@PathVariable Integer id) {
        try {
            placeService.deletePlace(id);
            return ResponseEntity.noContent().build();
        } catch (PlaceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}