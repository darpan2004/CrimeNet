package org.example.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/ping")
    public String ping() {
        logger.info("DEBUG: Ping endpoint called");
        return "PONG - Server is running!";
    }

    @PostMapping("/echo")
    public String echo(@RequestBody String body) {
        logger.info("DEBUG: Echo endpoint called with body: " + body);
        return "ECHO: " + body;
    }
} 