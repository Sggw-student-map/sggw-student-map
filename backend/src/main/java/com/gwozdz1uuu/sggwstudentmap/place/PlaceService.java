package com.gwozdz1uuu.sggwstudentmap.place;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;

    public List<PlaceResponse> getAllPlaces() {
        return placeRepository.findAll()
                .stream()
                .map(PlaceResponse::from)
                .toList();
    }

    public PlaceResponse getPlaceById(Integer id) {
        return placeRepository.findById(id)
                .map(PlaceResponse::from)
                .orElseThrow(() -> new PlaceNotFoundException(id));
    }

    public PlaceResponse getPlaceByName(String name) {
        return placeRepository.findByName(name)
                .map(PlaceResponse::from)
                .orElseThrow(() -> new PlaceNotFoundException(name));
    }

    public List<PlaceResponse> getPlacesByPattern(String pattern) {
        return placeRepository.findByNameContainingIgnoreCase(pattern)
                .stream()
                .map(PlaceResponse::from)
                .toList();
    }

    public PlaceResponse createPlace(PlaceRequest request) {
        Place place = new Place();
        place.setName(request.name());
        place.setLatitude(request.latitude());
        place.setLongitude(request.longitude());
        return PlaceResponse.from(placeRepository.save(place));
    }

    public PlaceResponse updatePlace(Integer id, PlaceRequest request) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new PlaceNotFoundException(id));
        place.setName(request.name());
        place.setLatitude(request.latitude());
        place.setLongitude(request.longitude());
        return PlaceResponse.from(placeRepository.save(place));
    }

    public void deletePlace(Integer id) {
        if (!placeRepository.existsById(id)) {
            throw new PlaceNotFoundException(id);
        }
        placeRepository.deleteById(id);
    }
}