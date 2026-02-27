package dev.dead.springintegration.Integration.nums;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.file.FileHeaders;
import org.springframework.messaging.handler.annotation.Header;

@MessagingGateway(defaultRequestChannel = "numInChannel")
public interface NumberFileWriterGateway {


    void writeNumbersToFile(@Header(FileHeaders.FILENAME) String fileName,
                            Integer... numbers);
}
