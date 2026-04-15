package com.example.mvcjsp.controller;

import com.example.mvcjsp.model.User;
import com.example.mvcjsp.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    private final UserRepository repo;

    public UserController(UserRepository repo){
        this.repo = repo;
    }

    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("users", repo.findAll());
        model.addAttribute("user", new User());
        return "index";
    }

    @PostMapping("/add")
    public String add(User user){
        repo.save(user);
        return "redirect:/";
    }
}
