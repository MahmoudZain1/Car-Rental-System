package com.carrental.repository;

import com.carrental.entity.Cars;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CarsRepository extends JpaRepository<Cars, Long> {

}
