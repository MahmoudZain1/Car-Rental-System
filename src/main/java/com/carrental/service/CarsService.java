package com.carrental.service;

import com.carrental.dto.CarDTO;
import com.carrental.dto.mappers.CarMapper;
import com.carrental.entity.Cars;
import com.carrental.repository.CarsRepository;
import jakarta.transaction.Transactional;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class CarsService {
    private final CarsRepository carsRepository;
    private final CarMapper carMapper;
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String UPLOAD_DIR = "src/main/resources/static";
    private final Logger logger = LoggerFactory.getLogger(CarsService.class);

    public CarsService(CarsRepository carsRepository, CarMapper carMapper) {
        this.carsRepository = carsRepository;
        this.carMapper = carMapper;
    }

    public CarDTO addCar(CarDTO carDTO) throws ServiceException {
        try {
            if (carDTO == null) {
                throw new IllegalArgumentException("CarDTO cannot be null");
            }
            Cars car = carMapper.toEntity(carDTO);
            Cars savedCar = carsRepository.save(car);
            return carMapper.toDTO(savedCar);
        } catch (Exception e) {
            logger.error("Failed to save car: {}", e.getMessage(), e);
            throw new ServiceException("Failed to save car: " + e.getMessage(), e);
        }
    }

    public Cars saveCarEntity(Cars car, MultipartFile file) throws ServiceException {
        try {
            if (car == null) {
                throw new IllegalArgumentException("Car entity cannot be null");
            }

            if (file != null && !file.isEmpty()) {
                if (file.getSize() > MAX_IMAGE_SIZE) {
                    throw new ServiceException("Image size exceeds maximum limit of 5MB");
                }

                Path uploadDir = Paths.get(UPLOAD_DIR, "uploads");
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path filePath = uploadDir.resolve(fileName);
                Files.write(filePath, file.getBytes());

                car.setImagePath("/uploads/" + fileName);
                logger.info("Saved image to: {} with db path: {}", filePath, car.getImagePath());
            }

            return carsRepository.save(car);
        } catch (Exception e) {
            logger.error("Failed to save car entity: {}", e.getMessage(), e);
            throw new ServiceException("Failed to save car entity: " + e.getMessage(), e);
        }
    }

    @Cacheable(value = "CarCache", key = "#id")
    public CarDTO findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Car ID cannot be null");
        }
        Optional<Cars> carOptional = carsRepository.findById(id);
        if (carOptional.isEmpty()) {
            logger.warn("Car with id {} not found", id);
            throw new ServiceException("Car not found with id: " + id);
        }
        return carMapper.toDTO(carOptional.get());
    }

    @Cacheable(value = "CarCache")
    @CacheEvict(value = "CarCache", allEntries = true)
    public List<CarDTO> getAllCars() {
        List<Cars> carsList = carsRepository.findAll();
        return carMapper.toDTOList(carsList);
    }

    @Cacheable(value = "CarCache")
    @CacheEvict(value = "CarCache", allEntries = true)
    public long sizeCar() {
        return carsRepository.count();
    }
}