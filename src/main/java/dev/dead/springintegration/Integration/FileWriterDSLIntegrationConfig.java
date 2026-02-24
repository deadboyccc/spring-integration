package dev.dead.springintegration.Integration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.file.support.FileExistsMode;

import java.io.File;

@Configuration
public class FileWriterDSLIntegrationConfig {

    @Bean
    public IntegrationFlow fileWriterFlow() {
        return IntegrationFlow
                .from(MessageChannels.direct("textInChannel"))
                .<String, String>transform(String::toUpperCase)
                .handle(Files
                        .outboundAdapter(new File("./output"))
                        .fileExistsMode(FileExistsMode.APPEND)
                        .appendNewLine(true))
                .get();
    }
}
