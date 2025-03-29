package com.carrental.service;

import com.carrental.repository.CarsRepository;
import com.carrental.entity.Cars;
import jakarta.transaction.Transactional;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CarsService {
    private final CarsRepository carsRepository;
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;
    private final Logger logger = LoggerFactory.getLogger(CarsService.class);


    public CarsService(CarsRepository carsRepository) {
        this.carsRepository = carsRepository;
    }

    public Cars addCar(Cars car) throws ServiceException {
        try {
            if (car.getImage() != null && car.getImage().length > MAX_IMAGE_SIZE) {
                throw new ServiceException("Image size exceeds maximum limit of 5MB");
            }
            return carsRepository.save(car);
        } catch (Exception e) {
            throw new ServiceException("Failed to save car: " + e.getMessage(), e);
        }
    }


    public Cars findById(long id) {
        return carsRepository.findById(id).orElse(null);
    }

    @Cacheable(value = "CarCache")
    public List<Cars> getAllCars() {
        List<Cars> carsList =  carsRepository.findAll();
        return carsList;
    }
}