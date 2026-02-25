package dev.dead.springintegration.Integration;

import lombok.RequiredArgsConstructor;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileWriterSender {

    private final MessageChannel textInChannel;

    @Scheduled(fixedRate = 1000)
    public void sendText() { // Removed the 'String text' parameter
        String payload = "Message sent at: " + System.currentTimeMillis();

        textInChannel.send(MessageBuilder.withPayload(payload)
                .build());
    }
}
