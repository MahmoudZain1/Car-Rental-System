package com.carrental.config;

import com.carrental.dto.CarDTO;
import com.carrental.service.CarsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Configuration
@EnableCaching
public class CacheConfig {

    private final CacheManager cacheManager;
    private final CarsService carsService;

    public CacheConfig(CacheManager cacheManager, CarsService carsService) {
        this.cacheManager = cacheManager;
        this.carsService = carsService;
    }

    @PostConstruct
    public void preloadCache() {
        try {
            Cache cache = cacheManager.getCache("CarCache");
            if (cache != null) {
                List<CarDTO> carsList = carsService.getAllCars();
                carsList.forEach(carDTO -> cache.put(carDTO.getId(), carDTO));
                log.info("Cache preloaded successfully with {} cars", carsList.size());
            } else {
                log.error("Cache CarCache not found");
            }
        } catch (Exception e) {
            log.error("Error preloading cache: {}", e.getMessage(), e);
        }
    }
}