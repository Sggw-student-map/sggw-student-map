package com.gwozdz1uuu.sggwstudentmap.repository;

import com.gwozdz1uuu.sggwstudentmap.entity.Uzytkownik;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UzytkownikRepository extends JpaRepository<Uzytkownik, Integer> {
}
