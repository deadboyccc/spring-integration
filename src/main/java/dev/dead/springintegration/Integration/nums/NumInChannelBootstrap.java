package dev.dead.springintegration.Integration.nums;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class NumInChannelBootstrap {

    private final NumberFileWriterGateway numberFileWriterGateway;

    @Scheduled(fixedRate = 1000)
    public void sendNumbersToFile() {
        var numbers = IntStream.rangeClosed(1, 30)
                .map(i -> (int) (Math.random() * 100_000) + 1)
                .boxed()
                .toArray(Integer[]::new);
        numberFileWriterGateway.writeNumbersToFile("numbers.txt", numbers);
    }

}
