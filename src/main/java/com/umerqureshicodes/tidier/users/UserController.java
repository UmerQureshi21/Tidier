package com.umerqureshicodes.tidier.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;


    @CrossOrigin(origins = "#{@environment.getProperty('frontend.host')}")
    @PostMapping("/register")
    public UserResponseDTO register(@RequestBody UserRequestDTO userRequestDTO) {
        return userService.createUser(userRequestDTO);
    }

    @GetMapping("/test")
    public String test(@RequestBody String random) {
        return random;
    }

}
