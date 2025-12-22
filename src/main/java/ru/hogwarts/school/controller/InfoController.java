package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.IntStream;

@RestController
public class InfoController {

    @Value("${server.port}")
    private String serverPort;

    @GetMapping("/port")
    private String getPort() {
        return serverPort;
    }

    @GetMapping("/stream-sum")
    public int streamSum() {
        return IntStream.rangeClosed(1, 1000000)
                .parallel()
                .sum();
    }
}
