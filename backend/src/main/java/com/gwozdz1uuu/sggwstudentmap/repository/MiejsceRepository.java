package com.gwozdz1uuu.sggwstudentmap.repository;

import com.gwozdz1uuu.sggwstudentmap.entity.Miejsce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MiejsceRepository extends JpaRepository<Miejsce, Integer> {
}
