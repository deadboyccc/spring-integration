package dev.dead.springintegration.controllers;

import dev.dead.springintegration.Integration.FileWriterGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
public class SecurityController {
    private final FileWriterGateway fileWriterGateway;


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

    @GetMapping("integration")
    public Mono<String> integrationTest(
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = (userDetails != null) ? userDetails.getUsername() : "Anonymous";
        fileWriterGateway.writeToFile("integration_test.txt", "User: " + username);
        return Mono.just("Integration test completed for user: " + username);

    }
}
