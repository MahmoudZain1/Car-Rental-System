package com.carrental.service;

import com.carrental.dto.CarDTO;
import com.carrental.dto.mappers.CarMapper;
import com.carrental.repository.CarsRepository;
import com.carrental.entity.Cars;
import jakarta.transaction.Transactional;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CarsService {
    private final CarsRepository carsRepository;
    private final CarMapper carMapper;
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;
    private final Logger logger = LoggerFactory.getLogger(CarsService.class);

    public CarsService(CarsRepository carsRepository, CarMapper carMapper) {
        this.carsRepository = carsRepository;
        this.carMapper = carMapper;
    }

    public CarDTO addCar(CarDTO carDTO) throws ServiceException {
        try {
            Cars car = carMapper.toEntity(carDTO);
            if (car.getImage() != null && car.getImage().length > MAX_IMAGE_SIZE) {
                throw new ServiceException("Image size exceeds maximum limit of 5MB");
            }
            Cars savedCar = carsRepository.save(car);
            return carMapper.toDTO(savedCar);
        } catch (Exception e) {
            logger.error("Failed to save car: {}", e.getMessage(), e);
            throw new ServiceException("Failed to save car: " + e.getMessage(), e);
        }
    }

        public CarDTO findById(Long id) {
        Optional<Cars> carOptional = carsRepository.findById(id);
        if (carOptional.isEmpty()) {
            logger.warn("Car with id {} not found", id);
            throw new ServiceException("Car not found with id: " + id);
        }
        return carMapper.toDTO(carOptional.get());
    }

    @Cacheable(value = "CarCache")
    public List<CarDTO> getAllCars() {
        List<Cars> carsList = carsRepository.findAll();
        return carMapper.toDTOList(carsList);
    }
}