package dev.dead.springintegration.Integration.nums;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.messaging.MessageChannel;

import java.io.File;
import java.util.concurrent.Executors;

@Configuration
public class NumbersIntegrationFlows {

    @Bean
    public MessageChannel numInChannel() {
        // Using Virtual Threads to handle concurrent file I/O operations efficiently
        return new ExecutorChannel(Executors.newVirtualThreadPerTaskExecutor());
    }

    @Bean
    public IntegrationFlow numInFlow() {
        return IntegrationFlow.from(numInChannel())
                // 1. Break the input (e.g. List<Integer>) into individual messages
                .split()

                // 2. Route messages to subflows based on the number being even or odd
                .<Integer, String>route(
                        n -> (n % 2 == 0) ? "even" : "odd",
                        mapping -> mapping
                                // Even Subflow: Writes to output/even.txt
                                .subFlowMapping("even", sf -> sf
                                        .transform(Object::toString)
                                        .handle(Files.outboundAdapter(new File("./output"))
                                                .fileNameGenerator(msg -> "even.txt")
                                                .fileExistsMode(FileExistsMode.APPEND)
                                                .appendNewLine(true)
                                        )
                                )
                                // Odd Subflow: Writes to output/odd.txt
                                .subFlowMapping("odd", sf -> sf
                                        .transform(Object::toString)
                                        .handle(Files.outboundAdapter(new File("./output"))
                                                .fileNameGenerator(msg -> "odd.txt")
                                                .fileExistsMode(FileExistsMode.APPEND)
                                                .appendNewLine(true)
                                        )
                                )
                )
                .get();
    }
}