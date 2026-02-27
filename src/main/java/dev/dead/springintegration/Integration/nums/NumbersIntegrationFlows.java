package dev.dead.springintegration.Integration.nums;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;

import java.util.concurrent.Executors;

@Configuration
public class NumbersIntegrationFlows {

    @Bean
    public MessageChannel numInChannel() {
        // Using Virtual Threads for high-concurrency processing
        return new ExecutorChannel(Executors.newVirtualThreadPerTaskExecutor());
    }

    @Bean
    public IntegrationFlow numInFlow() {
        return IntegrationFlow.from(numInChannel())
                // 1. Split the collection (e.g., List<Integer>) into individual messages
                .split()

                // 2. Route based on the payload value
                .<Integer, String>route(
                        // Router logic: returns the 'key' used for mapping
                        n -> (n % 2 == 0) ? "even" : "odd",

                        // Mapping logic: connects the key to a specific subflow
                        mapping -> mapping
                                .subFlowMapping("even", sf -> sf.handle((payload, headers) -> {
                                    System.out.println(Thread.currentThread() + " -> Even: " + payload);
                                    return null;
                                }))
                                .subFlowMapping("odd", sf -> sf.handle((payload, headers) -> {
                                    System.out.println(Thread.currentThread() + " -> Odd: " + payload);
                                    return null;
                                }))
                )
                .get();
    }
}
