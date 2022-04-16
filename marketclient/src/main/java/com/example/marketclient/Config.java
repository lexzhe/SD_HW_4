package com.example.marketclient;

import com.example.marketclient.model.ClientModel;
import com.example.marketclient.model.InMemoryClientModel;
import com.example.marketclient.service.StockService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public ClientModel clientDao(final StockService stockClient) {
        return new InMemoryClientModel(stockClient);
    }
}
