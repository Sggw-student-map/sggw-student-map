package com.gwozdz1uuu.sggwstudentmap.friendship;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "friendships")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(FriendshipId.class)
public class Friendship {

    @Id
    @Column(name = "user_id1")
    private Integer userId1;

    @Id
    @Column(name = "user_id2")
    private Integer userId2;

    @Column(name = "status_of_friendship")
    private String status = "pending";

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
