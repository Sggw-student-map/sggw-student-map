package com.gwozdz1uuu.sggwstudentmap.feed;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "feed_post_urls")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedPostUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_post_id", nullable = false)
    private FeedPost feedPost;

    @Column(name = "url", nullable = false, length = 1000)
    private String url;
}