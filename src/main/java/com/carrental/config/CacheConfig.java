package com.carrental.config;


import com.carrental.entity.Cars;
import com.carrental.service.CarsService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
@EnableCaching
public class CacheConfig {

    public CacheManager cacheManager;
    public CarsService carsService;

    public CacheConfig(CacheManager cacheManager, CarsService carsService) {
        this.cacheManager = cacheManager;
        this.carsService = carsService;
    }

    @PostConstruct
    public void PerLoadCache(){
        try{
            Cache cache = cacheManager.getCache("CarCache");
            if (cache != null){
                List<Cars> carsList = carsService.getAllCars();
                carsList.forEach(cars ->  cache.put(cars.getId() , cars));
                log.info("Cache PErloaded Succesfuly -> " + carsList.size());
            }else {
                log.error("Error Cache Found");
            }
        } catch (Exception e) {
            log.error("Cache Perloading Error -> " + e.getMessage() + e);
        }
    }
}
