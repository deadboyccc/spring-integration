package dev.dead.springintegration.Integration.nums;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NumInChannelBootstrap {

    private final NumberFileWriter numberFileWriter;

    @Scheduled(fixedDelay = 5000)
    public void sendNumbersToFile() {
        Integer[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        numberFileWriter.writeNumbersToFile("numbers.txt", numbers);
    }

}
