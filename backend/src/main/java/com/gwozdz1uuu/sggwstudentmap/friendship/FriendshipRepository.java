package com.gwozdz1uuu.sggwstudentmap.friendship;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, FriendshipId> {

    // All accepted friends of a user (friendship can be in either column)
    @Query("""
        SELECT f FROM Friendship f
        WHERE (f.userId1 = :userId OR f.userId2 = :userId)
        AND f.status = 'accepted'
    """)
    List<Friendship> findAcceptedFriendships(@Param("userId") Integer userId);

    // Pending invites RECEIVED by user (user is userId2, sender is userId1)
    @Query("""
        SELECT f FROM Friendship f
        WHERE f.userId2 = :userId AND f.status = 'pending'
    """)
    List<Friendship> findPendingReceived(@Param("userId") Integer userId);

    // Pending invites SENT by user
    @Query("""
        SELECT f FROM Friendship f
        WHERE f.userId1 = :userId AND f.status = 'pending'
    """)
    List<Friendship> findPendingSent(@Param("userId") Integer userId);

    // Find friendship between two users regardless of order
    @Query("""
        SELECT f FROM Friendship f
        WHERE (f.userId1 = :a AND f.userId2 = :b)
           OR (f.userId1 = :b AND f.userId2 = :a)
    """)
    Optional<Friendship> findBetween(@Param("a") Integer a, @Param("b") Integer b);

    // All users who are NOT yet friends or pending with the current user
    @Query(value = """
        SELECT u.id, u.username, u.first_name, u.last_name FROM users u
        WHERE u.id != :userId
        AND u.id NOT IN (
            SELECT CASE WHEN f.user_id1 = :userId THEN f.user_id2 ELSE f.user_id1 END
            FROM friendships f
            WHERE f.user_id1 = :userId OR f.user_id2 = :userId
        )
    """, nativeQuery = true)
    List<Object[]> findUsersNotInFriendship(@Param("userId") Integer userId);

    @Query("""
        SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END
        FROM Friendship f
        WHERE ((f.userId1 = :userA AND f.userId2 = :userB)
            OR (f.userId1 = :userB AND f.userId2 = :userA))
        AND f.status = 'accepted'
    """)
    boolean areAcceptedFriends(@Param("userA") Integer userA, @Param("userB") Integer userB);
}
