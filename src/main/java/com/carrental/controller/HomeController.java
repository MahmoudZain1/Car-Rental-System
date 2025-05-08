package com.carrental.controller;



import com.carrental.dto.CarDTO;
import com.carrental.dto.UsersDTO;
import com.carrental.entity.Cars;
import com.carrental.service.CarsService;
import com.carrental.service.UserProfileService;
import com.itextpdf.forms.xfdf.Mode;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@AllArgsConstructor
public class HomeController {


    private final Logger logger = LoggerFactory.getLogger(HomeController.class);
    private final CarsService carsService;
    private final UserProfileService profileService;

    @GetMapping("/home")
    public String ShowHome(){
        return "/Home/home";
    }

    @GetMapping("/Book")
    public String ShowCarBook(){
        return "/Home/BookCar";
    }


    @GetMapping("/Profile")
    public String ShowProfile(Model model){

        UsersDTO usersDTO = profileService.GetCurrentUsr();
        model.addAttribute("user" , usersDTO);
        return "/Home/profile";
    }


    @GetMapping("/Cars")
    public String ShowCars(Model model){

        List<CarDTO> allCars = carsService.getAllCars();
        model.addAttribute("cars" , allCars);

        logger.info("List Cars Size : {}", carsService.sizeCar());
        return "Home/Cars";
    }




}
