package com.carrental.repository;

import com.carrental.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserProfileRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);

}
