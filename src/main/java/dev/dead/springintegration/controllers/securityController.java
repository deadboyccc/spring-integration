package dev.dead.springintegration.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class securityController {
        @GetMapping("/public")
        public String publicEndpoint() {
            return "This is a public endpoint.";
        }

        @GetMapping("/private")
        public String privateEndpoint() {
            return "This is a private endpoint.";
        }
}
