package com.example.marketclient.service;

import org.springframework.stereotype.Service;

@Service
public class StockService {

    private final RestService restService;

    public StockService(final RestService restService) {
        this.restService = restService;
    }

    public void modifyStock(final String companyName, final long quantityDelta) {
        String request = "modify-stock?name="+companyName+"&count=" + quantityDelta;
        restService.getString(request);
        System.out.println("Output for modify request: " + request);
    }

    public double queryPrice(final String name) {
        String resp = restService.getString("stock-info");
        final String[] split = resp.split("\\|");
        for (final String line : split) {
            if (line.contains("'" + name + "'")) {
                System.out.println("Line is: " + line);
                return Double.parseDouble(line.substring(line.lastIndexOf(' ')));
            }
        }
        throw new IllegalArgumentException("No stock " + name + " has been found on market");
    }
}
