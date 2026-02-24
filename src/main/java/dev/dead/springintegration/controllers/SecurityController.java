package dev.dead.springintegration.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class SecurityController {

    // Use UserDetails interface instead of the concrete User class for better compatibility
    @GetMapping("/public")
    public Mono<String> publicEndpoint(
            @AuthenticationPrincipal UserDetails user) {
        String username = (user != null) ? user.getUsername() : "Anonymous";
        return Mono.just("public + " + username);
    }

    @GetMapping("/private")
    public Mono<String> privateEndpoint() {
        return Mono.just("private");
    }

    @GetMapping("/admin/test")
    public Mono<String> adminEndpoint(Authentication authentication) {
        // Spring WebFlux will inject the Authentication object directly from the Reactor Context
        UserDetails user = (UserDetails) authentication.getPrincipal();
        return Mono.just("admin + " + user.getUsername());
    }
}
