package dev.dead.springintegration.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class SecurityController {
        @GetMapping("/public")
        public Mono<String> publicEndpoint() {
            return Mono.just("public");
        }

        @GetMapping("/private")
        public Mono<String> privateEndpoint() {
            return Mono.just("private");
        }

    @GetMapping("/admin/test")
    public Mono<String> adminEndpoint() {
        return Mono.just("admin");
    }
}
