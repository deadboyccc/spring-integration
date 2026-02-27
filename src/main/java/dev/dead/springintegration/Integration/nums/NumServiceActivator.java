package dev.dead.springintegration.Integration.nums;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;

import java.util.Arrays;

@Configuration
public class NumServiceActivator {

    @Bean
    public IntegrationFlow serviceActivatorFlow() {
        return IntegrationFlow
                .from("numInChannel")
                // Use the typed handle method to avoid manual casting
                .handle(Integer[].class, (payload, headers) -> {

                    Arrays.stream(payload)
                            .forEach(this::processNumber);

                    // Peek at headers if needed, then pass the payload along
                    return payload;
                })
                .get();
    }

    private void processNumber(Integer n) {
        System.out.println("Processing number: " + n);
    }
}
