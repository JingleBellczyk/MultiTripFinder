package org.dyploma.google;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GoogleService {

    private final String GOOGLE_API_KEY = "AIzaSyDwTRyNumOMyzDm95qZqvIC2nWhKx8wd8k";
    private final RestTemplate restTemplate;

    @Autowired
    public GoogleService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public String makeRequest(String requestUrl) {
        long startTime = System.currentTimeMillis();

        // Make the REST API request
        String response = restTemplate.getForObject(requestUrl, String.class);

        // Capture the end time
        long endTime = System.currentTimeMillis();

        // Calculate the time taken
        long timeTaken = endTime - startTime;

        // Print the time taken
        LoggerFactory.getLogger(GoogleService.class).info("Time taken for the request: {} milliseconds", timeTaken);
        LoggerFactory.getLogger(GoogleService.class).info("Response: {}", response);
        return response;
    }
}
