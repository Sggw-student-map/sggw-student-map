package com.gwozdz1uuu.sggwstudentmap.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserOnEventRepository extends JpaRepository<UserOnEvent, UserOnEventId> {
    Optional<UserOnEvent> findByIdEventuAndIdUsers(Integer idEventu, Integer idUsers);
    int countByIdEventu(Integer idEventu);
}