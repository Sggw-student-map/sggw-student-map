package com.gwozdz1uuu.sggwstudentmap.place;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NavigationResponse {
    private Integer placeId;
    private String placeName;
    private Double latitude;
    private Double longitude;
    private String googleMapsUrl;
}
