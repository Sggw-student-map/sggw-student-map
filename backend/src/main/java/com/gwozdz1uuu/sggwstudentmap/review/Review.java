package com.gwozdz1uuu.sggwstudentmap.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonProperty("place_id")
    private Integer placeId;

    @JsonProperty("user_id")
    private Integer userId;

    private Integer rating;
    
    @Column(length = 1000)
    private String comment;
    
    private String author;

    @JsonProperty("image_url")
    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewUrl> urls;
}