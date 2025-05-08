package com.carrental.controller;

import com.carrental.dto.UsersDTO;
import com.carrental.entity.PersonalInformation;
import com.carrental.entity.Users;
import com.carrental.service.CarsService;
import com.carrental.service.PdfService;
import com.carrental.service.UserProfileService;
import com.carrental.service.UserServiceImpl;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@AllArgsConstructor
public class ApiController {

    private final UserServiceImpl userServiceImpl;
    private final PdfService pdfService;
    private final UserProfileService userProService;
    private final HttpSession httpSession;
    private final CarsService carsService;




    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/EditUser/{id}")
    public String editUser(@PathVariable("id") Long id, Model model) {
        Optional<PersonalInformation> user = userServiceImpl.GetById(id);
        model.addAttribute("userid", user.orElse(null));
        return "EditUser";
    }

    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf() {
        try {
            byte[] pdfBytes = pdfService.generatePdf();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "users-report.pdf");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/AddUser")
    public String addUser(Model model) {
        model.addAttribute("Users", new PersonalInformation());
        return "AddUser";
    }


    @GetMapping("/Rentals")
    public String ShowRentals(){
        return "/Rentals";
    }

    @GetMapping("/Analytics")
    public String ShowAnalytics(){
        return "/Analytics";
    }




    @GetMapping("/shwo")
    public String show() {
        return "home/profile";
    }

    @PostMapping("/AddUser")
    public String saveUser(@Valid @ModelAttribute("Users") PersonalInformation user,
                           BindingResult result,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        if (result.hasErrors()) {
            return "AddUser";
        }

        String res = userServiceImpl.saveUser(user);
        if (res.equals("Done")) {
            redirectAttributes.addFlashAttribute("successMessage", "Users added successfully!");
            return "redirect:/Users";
        }
        model.addAttribute("errorMessage", res);
        return "AddUser";
    }

    @GetMapping("/Users")
    public String showUsers(
            @RequestParam(required = false) String searchName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Page<PersonalInformation> userPage = userServiceImpl.searchUsers(searchName, page, size);

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalUser", userPage.getTotalElements());
        model.addAttribute("totalCars" , carsService.sizeCar());
        model.addAttribute("searchName", searchName);

        return "DashBord";
    }


    @GetMapping("/Register")
    public String registerUser(Model model) {
        model.addAttribute("user", new Users());
        return "Profiles/Register";
    }

    @PostMapping("/Register")
    public String registerUser(@ModelAttribute("user") @Valid UsersDTO users, Model model) {
        try {
            userProService.saveUser(users);
            httpSession.setAttribute("email" ,users.getEmail());
            return "redirect:/ShowVerify";
        } catch (IllegalStateException e) {
            model.addAttribute("emailError", e.getMessage());
            return "Profiles/Register";
        }
    }


    @GetMapping("/Login")
    public String loginUser(Model model) {
        model.addAttribute("user", new Users());
        return "Profiles/login";
    }

    @GetMapping("/ShowVerify")
    public String ShowVerify(){
        return "Profiles/Verify";
    }



   @GetMapping("/Verify")
   public String Verifyed(@SessionAttribute("email")String email ,
                          @RequestParam("code") String Code , Model model){

        Boolean isVerifyed = userProService.verifyUserCode(email , Code);

        if(isVerifyed){
            return "redirect:/Login";
        }
        model.addAttribute("error" , "invalde code");
        return "Profiles/Verify";
   }


    @PostMapping("/login")
    public String loginForm(@RequestParam("username") String email,
                            @RequestParam("password") String password,
                            Model model, HttpSession session) {
        try {
            boolean isLogin = userProService.verifyLogin(email, password, session);

            if (isLogin) {
                return "redirect:/home";
            } else {
                model.addAttribute("error", "Invalid Email or Password");
                return "redirect:/Login";
            }
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "Profiles/login";
        }
    }




}
