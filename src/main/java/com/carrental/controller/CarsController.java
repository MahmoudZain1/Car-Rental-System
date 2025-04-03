package com.carrental.controller;

import com.carrental.dto.CarDTO;
import com.carrental.dto.mappers.CarMapper;
import com.carrental.entity.Cars;
import com.carrental.entity.enums.CarStatus;
import com.carrental.service.CarsService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/cars")
public class CarsController {
    private final CarsService carsService;
    private final CarMapper carMapper;
    private final Logger logger = LoggerFactory.getLogger(CarsController.class);

    public CarsController(CarsService carsService, CarMapper carMapper) {
        this.carsService = carsService;
        this.carMapper = carMapper;
    }

    @GetMapping("/add")
    public String showAddCarForm(Model model) {
        if (!model.containsAttribute("carDTO")) {
            model.addAttribute("carDTO", new CarDTO());
        }
        return "AddCars";
    }

    @PostMapping("/add")
    public String saveCar(
            @Valid @ModelAttribute("carDTO") CarDTO carDTO,
            BindingResult bindingResult,
            @RequestParam("imageFile") MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {

        try {
            logger.info("Attempting to save car: {} {} {}", carDTO.getBrand(), carDTO.getModel(), carDTO.getYear());

            if (bindingResult.hasErrors()) {
                logger.error("Validation errors found: {}", bindingResult.getAllErrors());
                return "AddCars";
            }

            if (imageFile != null && !imageFile.isEmpty()) {
                logger.info("Image file received, size: {} bytes", imageFile.getSize());
                Cars carEntity = carMapper.toEntity(carDTO);
                carEntity.setImage(imageFile.getBytes());
                carDTO = carMapper.toDTO(carEntity);
            }

            if (carDTO.getStatus() == null) {
                carDTO.setStatus(CarStatus.AVAILABLE);
            }

            CarDTO savedCarDTO = carsService.addCar(carDTO);
            logger.info("Car saved successfully with ID: {}", savedCarDTO.getId());

            redirectAttributes.addFlashAttribute("success", "Car added successfully");
            return "redirect:/cars";

        } catch (IOException e) {
            logger.error("Error processing image file: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error processing image file");
            return "redirect:/cars/add";
        } catch (Exception e) {
            logger.error("Error saving car: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error saving car: " + e.getMessage());
            return "redirect:/cars/add";
        }
    }

    @GetMapping
    public String listCars(Model model) {
        List<CarDTO> carsList = carsService.getAllCars();
        logger.info("Retrieved Cars List: {}", carsList);
        model.addAttribute("cars", carsList);
        return "Cars";
    }

    @GetMapping("/{id}")
    public String getCar(@PathVariable Long id, Model model) {
        CarDTO carDTO = carsService.findById(id);
        model.addAttribute("car", carDTO);
        return "CarDetails";
    }
}