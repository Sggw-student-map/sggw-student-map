package com.gwozdz1uuu.sggwstudentmap.user.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {
    Optional<UserRole> findByUser_Id(Integer userId);
    Optional<UserRole> findByUser_Username(String username);
    boolean existsByUser_Id(Integer userId);
}
