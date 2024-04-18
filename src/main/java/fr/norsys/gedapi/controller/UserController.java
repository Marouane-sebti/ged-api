package fr.norsys.gedapi.controller;

import fr.norsys.gedapi.model.User;
import fr.norsys.gedapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping("/registre")
    public int registre(User user) {
        return userService.registre(user);
    }
    @GetMapping("/findByUsername")
    public User findByUsername(String username) {
        return userService.findByUsername(username);
    }
    @GetMapping("/findById")
    public Optional<User> findById(int id) {
        return userService.findById(id);
    }
}
