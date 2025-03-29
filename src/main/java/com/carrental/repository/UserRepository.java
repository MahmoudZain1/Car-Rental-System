package com.carrental.repository;

import com.carrental.entity.PersonalInformation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<PersonalInformation,  Long> {

    Optional<PersonalInformation> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<PersonalInformation> findByPhone(String phone);

    @Query(value = "SELECT * FROM personal_information p WHERE " +
            "(:name IS NULL OR LOWER(p.first_name) LIKE LOWER(('%' || :name || '%')) " +
            "OR LOWER(p.last_name) LIKE LOWER(('%' || :name || '%'))) ",
            nativeQuery = true)
    Page<PersonalInformation> findAllBySearch(
            @Param("name") String name,
            Pageable pageable
    );

}
