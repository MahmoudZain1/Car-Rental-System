package com.carrental.controller;



import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String ShowHome(){
        return "/Home/home";
    }

    @GetMapping("/Book")
    public String ShowCarBook(){
        return "/Home/BookCar";
    }


    @GetMapping("/Profile")
    public String ShowProfile(){
        return "/Home/profile";
    }


    @GetMapping("/Cars")
    public String ShowCars(){
        return "/Home/Cars";
    }




}
