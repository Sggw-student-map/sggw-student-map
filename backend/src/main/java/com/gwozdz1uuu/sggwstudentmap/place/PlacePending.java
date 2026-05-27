package com.gwozdz1uuu.sggwstudentmap.place;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "places_pending")
@Data
@NoArgsConstructor
public class PlacePending {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private Double latitude;
    private Double longitude;
    private String description;

    private Integer placeId;

    @Enumerated(EnumType.STRING)
    private PendingActionType actionType;

    @Enumerated(EnumType.STRING)
    private PendingStatus status = PendingStatus.PENDING;

    private Integer requestedByUserId;

    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}