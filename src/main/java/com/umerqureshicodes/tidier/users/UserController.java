package com.umerqureshicodes.tidier.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("/register")
    public UserResponseDTO register(@RequestBody UserRequestDTO userRequestDTO) {
        return userService.createUser(userRequestDTO);
    }

    @PostMapping("/login")
    public UserResponseDTO login(@RequestBody UserRequestDTO dto) {
        return userService.login(dto) ;
    }

    @GetMapping("/user")
    public UserResponseDTO getUser(@AuthenticationPrincipal AppUser appUser){
        return userService.getUser(appUser.getUsername());
    }

    @GetMapping("/test")
    public String test(@RequestBody String random) {
        return random;
    }

}
