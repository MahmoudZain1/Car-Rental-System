package com.carrental.repository;

import com.carrental.entity.Role;
import com.carrental.entity.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRoleRepository extends JpaRepository<Role , Integer> {

    Optional<Role> findByName(UserRole name);
}
