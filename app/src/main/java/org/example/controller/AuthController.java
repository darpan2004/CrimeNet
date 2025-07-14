package org.example.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.example.entity.User;
import org.example.service.JwtService;
import org.example.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        logger.info("DEBUG: Test endpoint called");
        return ResponseEntity.ok("Auth controller is working!");
    }

    @GetMapping("/public-test")
    public ResponseEntity<String> publicTest() {
        logger.info("DEBUG: Public test endpoint called");
        return ResponseEntity.ok("Public endpoint is accessible!");
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        System.out.println("DEBUG: Login endpoint called");
        System.out.println("DEBUG: Request body: " + loginRequest);
        
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        System.out.println("DEBUG: Username: " + username);
        System.out.println("DEBUG: Password received: '" + password + "'");
        System.out.println("DEBUG: Password length: " + (password != null ? password.length() : "null"));

        try {
            System.out.println("DEBUG: Attempting authentication");
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );
            System.out.println("DEBUG: Authentication successful");

            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("DEBUG: SecurityContext set");

            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            System.out.println("DEBUG: User found: " + user.getUsername());

            String jwt = jwtService.generateToken(user);
            System.out.println("DEBUG: JWT generated");

            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("type", "Bearer");
            response.put("user", user);

            System.out.println("DEBUG: Login successful for user: " + username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("DEBUG: Login failed: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
      
        Optional<User> usee=userService.findByUsername(username);
        System.out.println("darapa "+username+" dfsd "+usee);
        return userService.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String jwt = jwtService.generateToken(user);

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("type", "Bearer");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/test-echo")
    public ResponseEntity<Map<String, String>> testEcho(@RequestBody Map<String, String> body) {
        System.out.println("DEBUG: test-echo called");
        return ResponseEntity.ok(body);
    }

    @PostMapping("/test-password")
    public ResponseEntity<Map<String, String>> testPassword(@RequestBody Map<String, String> body) {
        String password = body.get("password");
        String hashedPassword = "$2a$10$DdpAFFWX2C1Ku8RxmWXQV.A5cqIMHnhQwVRwcUQe2bL0UtllFjW/O";
        
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean matches = encoder.matches(password, hashedPassword);
        
        Map<String, String> response = new HashMap<>();
        response.put("password", password);
        response.put("hashedPassword", hashedPassword);
        response.put("matches", String.valueOf(matches));
        response.put("passwordLength", String.valueOf(password != null ? password.length() : 0));
        
        System.out.println("DEBUG: Password test - Input: '" + password + "', Matches: " + matches);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/debug-users")
    public ResponseEntity<Map<String, Object>> debugUsers() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<User> users = userService.findAll();
            List<Map<String, Object>> userList = new ArrayList<>();
            
            for (User user : users) {
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("username", user.getUsername());
                userInfo.put("email", user.getEmail());
                userInfo.put("passwordHash", user.getPassword());
                userInfo.put("role", user.getRole());
                userList.add(userInfo);
            }
            
            response.put("users", userList);
            response.put("count", users.size());
            
        } catch (Exception e) {
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/find-password")
    public ResponseEntity<Map<String, Object>> findPassword(@RequestBody Map<String, String> body) {
        String hashedPassword = "$2a$10$DdpAFFWX2C1Ku8RxmWXQV.A5cqIMHnhQwVRwcUQe2bL0UtllFjW/O";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        Map<String, Object> response = new HashMap<>();
        List<String> testPasswords = Arrays.asList(
            "testpass123",
            "testpass",
            "test123",
            "password",
            "123456",
            "admin123",
            "police123",
            "john123",
            "testuser",
            "test",
            "pass123"
        );
        
        List<Map<String, String>> results = new ArrayList<>();
        for (String testPass : testPasswords) {
            boolean matches = encoder.matches(testPass, hashedPassword);
            Map<String, String> result = new HashMap<>();
            result.put("password", testPass);
            result.put("matches", String.valueOf(matches));
            results.add(result);
            
            if (matches) {
                System.out.println("DEBUG: Found matching password: '" + testPass + "'");
            }
        }
        
        response.put("results", results);
        response.put("hash", hashedPassword);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test-seeded-password")
    public ResponseEntity<Map<String, Object>> testSeededPassword() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Get the testuser from database
            User testUser = userService.findByUsername("testuser")
                    .orElseThrow(() -> new RuntimeException("testuser not found"));
            
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            
            // Test the exact password that should work
            String correctPassword = "testpass123";
            boolean matches = encoder.matches(correctPassword, testUser.getPassword());
            
            response.put("username", testUser.getUsername());
            response.put("storedHash", testUser.getPassword());
            response.put("testPassword", correctPassword);
            response.put("matches", matches);
            response.put("message", matches ? "Password matches!" : "Password does not match!");
            
            System.out.println("DEBUG: Testing seeded password for testuser");
            System.out.println("DEBUG: Stored hash: " + testUser.getPassword());
            System.out.println("DEBUG: Test password: '" + correctPassword + "'");
            System.out.println("DEBUG: Matches: " + matches);
            
        } catch (Exception e) {
            response.put("error", e.getMessage());
            e.printStackTrace();
        }
        
        return ResponseEntity.ok(response);
    }
} 