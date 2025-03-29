package com.carrental.controller;

import com.carrental.dto.CarDTO;
import com.carrental.repository.CarsRepository;
import com.carrental.entity.Cars;
import com.carrental.entity.enums.CarStatus;
import com.carrental.service.CarsService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class CarsController {
    private final CarsService carsService;
    private final CarsRepository carsRepository;
    private final Logger logger = LoggerFactory.getLogger(CarsController.class);

    public CarsController(CarsService carsService, CarsRepository carsRepository) {
        this.carsService = carsService;
        this.carsRepository = carsRepository;
    }

    @GetMapping("/AddCars")
    public String showAddCarForm(Model model) {
        if (!model.containsAttribute("cars")) {
            model.addAttribute("cars", new Cars());
        }
        return "AddCars";
    }

    @PostMapping("/CarsAdd")
    public String saveCars(
            @Valid @ModelAttribute("cars") Cars car,
            BindingResult bindingResult,
            @RequestParam("imageFile") MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {

        try {
            logger.info("Attempting to save car: {} {} {}", car.getBrand(), car.getModel(), car.getYear());

            if (bindingResult.hasErrors()) {
                logger.error("Validation errors found: {}", bindingResult.getAllErrors());
                return "AddCars";
            }

            if (imageFile != null && !imageFile.isEmpty()) {
                logger.info("Image file received, size: {} bytes", imageFile.getSize());
                car.setImage(imageFile.getBytes());
            }

            if (car.getStatus() == null) {
                car.setStatus(CarStatus.AVAILABLE);
            }

            Cars savedCar = carsService.addCar(car);
            logger.info("Car saved successfully with ID: {}", savedCar.getId());

            redirectAttributes.addFlashAttribute("success", "Car added successfully");
            return "redirect:/Cars";

        } catch (IOException e) {
            logger.error("Error processing image file: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error processing image file");
            return "redirect:/AddCars";
        } catch (Exception e) {
            logger.error("Error saving car: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error saving car: " + e.getMessage());
            return "redirect:/AddCars";
        }
    }



    @GetMapping("/cars")
    public String listCars(Model model) {
        List<Cars> carsList = carsService.getAllCars();
        logger.info("Retrieved Cars List: {}", carsList);
        model.addAttribute("cars", carsList);
        return "Cars";
    }



    @GetMapping("/{id}")
    public ResponseEntity<CarDTO> getCar(@PathVariable Long id) {
        Cars car = carsService.findById(id);
        if (car != null) {
            CarDTO carDTO = CarDTO.builder()
                    .id(car.getId())
                    .brand(car.getBrand())
                    .model(car.getModel())
                    .year(car.getYear())
                    .color(car.getColor())
                    .carType(car.getCarType())
                    .transmission(car.getTransmission())
                    .mileage(car.getMileage())
                    .status(car.getStatus())
                    .pricePerDay(car.getPricePerDay())
                    .description(car.getDescription())
                    .build();

            return ResponseEntity.ok(carDTO);
        }

        return ResponseEntity.notFound().build();
    }




}


