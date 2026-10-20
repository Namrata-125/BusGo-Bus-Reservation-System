package com.namrata.busreservation.controller;

import com.namrata.busreservation.entity.User;
import com.namrata.busreservation.service.UserService;
import com.namrata.busreservation.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {

        try {
            User savedUser = userService.registerUser(user);

            savedUser.setPassword(null);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedUser);

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {

        boolean loginSuccessful = userService.loginUser(
                user.getEmail(),
                user.getPassword()
        );

        if (loginSuccessful) {

            String token = jwtUtil.generateToken(user.getEmail());

            var loggedInUser = userService
                    .findByEmail(user.getEmail())
                    .get();

            loggedInUser.setPassword(null);

            return ResponseEntity.ok(
                    new LoginResponse(
                            "Login successful",
                            token,
                            loggedInUser
                    )
            );
        }

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Invalid email or password");
    }

    @GetMapping("/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {

        var userOptional = userService.findByEmail(email);

        if (userOptional.isPresent()) {

            User user = userOptional.get();
            user.setPassword(null);

            return ResponseEntity.ok(user);
        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("User not found");
    }

    public static class LoginResponse {

        private String message;
        private String token;
        private User user;

        public LoginResponse(String message, String token, User user) {
            this.message = message;
            this.token = token;
            this.user = user;
        }

        public String getMessage() {
            return message;
        }

        public String getToken() {
            return token;
        }

        public User getUser() {
            return user;
        }
    }
}