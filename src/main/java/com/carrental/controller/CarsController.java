package com.carrental.controller;

import com.carrental.dto.CarDTO;
import com.carrental.dto.mappers.CarMapper;
import com.carrental.entity.Cars;
import com.carrental.entity.enums.CarStatus;
import com.carrental.service.CarsService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class CarsController {
    private final CarsService carsService;
    private final CarMapper carMapper;
    private final Logger logger = LoggerFactory.getLogger(CarsController.class);
    private static final String[] ALLOWED_IMAGE_TYPES = {"image/jpeg", "image/png"};
    private static final String UPLOAD_DIR = "src/main/resources/static";

    public CarsController(CarsService carsService, CarMapper carMapper) {
        this.carsService = carsService;
        this.carMapper = carMapper;
    }


    @GetMapping("/PageHome")
    public String pageHome() {
        return "Home/home";
    }


    @GetMapping("/AddCars")
    public String showAddCarForm(Model model) {
        if (!model.containsAttribute("cars")) {
            model.addAttribute("cars", new CarDTO());
        }
        return "AddCars";
    }


    @PostMapping("/CarsAdd")
    public String saveCar(
            @Valid @ModelAttribute("cars") CarDTO carDTO,
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
                String contentType = imageFile.getContentType();
                if (!isValidImageType(contentType)) {
                    logger.error("Invalid image type: {}", contentType);
                    redirectAttributes.addFlashAttribute("error", "Only JPEG or PNG images are allowed");
                    return "redirect:/AddCars";
                }
            }

            Cars carEntity = carMapper.toEntity(carDTO);
            if (carDTO.getStatus() == null) {
                carEntity.setStatus(CarStatus.AVAILABLE);
            }

            Cars savedCar = carsService.saveCarEntity(carEntity, imageFile);
            logger.info("Car saved successfully with ID: {}", savedCar.getId());

            redirectAttributes.addFlashAttribute("success", "Car added successfully");
            return "redirect:/PageHome";

        } catch (Exception e) {
            logger.error("Error saving car: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error saving car: " + e.getMessage());
            return "redirect:/AddCars";
        }
    }


    @GetMapping("/cars/all")
    public String listCars(Model model) {
        List<CarDTO> carsList = carsService.getAllCars();
        logger.info("Retrieved {} cars", carsList.size());
        model.addAttribute("cars", carsList);
        return "Cars";
    }


    @GetMapping("/cars/{id}")
    public String getCar(@PathVariable Long id, Model model) {
        try {
            CarDTO carDTO = carsService.findById(id);
            logger.info("Cars retrieving car :{}" , carDTO.getImagePath());
            model.addAttribute("car", carDTO);
            return "CarDetails";
        } catch (Exception e) {
            logger.error("Error retrieving car with ID {}: {}", id, e.getMessage());
            return "redirect:/cars/all";
        }
    }



    @GetMapping("/image/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        try {
            CarDTO carDTO = carsService.findById(id);
            if (carDTO.getImagePath() == null) {
                logger.warn("No image found for car ID: {}", id);
                return ResponseEntity.notFound().build();
            }

            String imagePath = carDTO.getImagePath();

            Path fullPath = Paths.get(UPLOAD_DIR, imagePath.substring(1));
            logger.info("Attempting to load image from: {}", fullPath);

            if (!Files.exists(fullPath)) {
                logger.error("Image file not found at path: {}", fullPath);
                return ResponseEntity.notFound().build();
            }

            byte[] imageBytes = Files.readAllBytes(fullPath);

            MediaType contentType = imagePath.endsWith(".png") ?
                    MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(contentType);
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving image for car ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    private boolean isValidImageType(String contentType) {
        if (contentType == null) {
            return false;
        }
        for (String allowedType : ALLOWED_IMAGE_TYPES) {
            if (allowedType.equalsIgnoreCase(contentType)) {
                return true;
            }
        }
        return false;
    }
}