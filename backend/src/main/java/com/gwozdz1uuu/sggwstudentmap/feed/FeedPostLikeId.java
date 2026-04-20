package com.gwozdz1uuu.sggwstudentmap.feed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedPostLikeId implements Serializable {
    private Integer postId;
    private Integer userId;
}
