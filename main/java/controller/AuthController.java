package controller;


import jwt.JwtUtil;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import service.UserService;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public String login(@RequestParam(required = false) String email,
                        @RequestParam(required = false) String phone,
                        @RequestParam String password) {


        List<User> userOpt = email != null ? userService.findByEmail(email) : userService.findByPhone(phone);
        User user =  userOpt.stream().filter(Objects::nonNull).findFirst().get();

        if (userService.validatePassword(user, password)) {
            Long userId = user.getId();
            return jwtUtil.generateToken(userId);
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }
}

