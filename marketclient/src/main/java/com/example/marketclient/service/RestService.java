package com.example.marketclient.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Service
public class RestService {

    @Value("${stockserver_port}")
    public Integer serverPort;

    private final RestTemplate restTemplate;

    public RestService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(500))
                .setReadTimeout(Duration.ofSeconds(500))
                .build();
    }

    public boolean pingStock() {
        String url = "http://localhost:"+ serverPort + "/ping";
        System.out.println("Response for ping is: " + this.restTemplate.getForObject(url, String.class));
        return true;
    }

    public String getString(String request) {
        String url = "http://localhost:"+ serverPort + "/" + request;
        return this.restTemplate.getForObject(url, String.class);
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }
}