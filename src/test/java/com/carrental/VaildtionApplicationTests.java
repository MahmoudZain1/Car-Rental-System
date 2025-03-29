package com.carrental;

import com.carrental.entity.Cars;
import com.carrental.service.CarsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class VaildtionApplicationTests {
	@Autowired
	private CarsService carsService;

	@Test
	void contextLoads() {
	}


//	@Test
//	void testCarsService() {
//		assertNotNull(carsService, "CarsService should not be null");
//		List<Cars> cars = carsService.getAllCars();
//		assertNotNull(cars, "The car list should not be null");
//		assertFalse(cars.isEmpty(), "The car list should not be empty");
//		System.out.println("Number of cars: " + cars.size());
//		assertEquals(3, cars.size());
//		assertEquals(2, cars.size(), "Expected 2 cars in the list");
//	}

}
