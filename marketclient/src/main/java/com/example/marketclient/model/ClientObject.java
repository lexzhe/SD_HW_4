package com.example.marketclient.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Getter
@Setter
public class ClientObject {
    private final String name;
    private double funds;
    private final Map<String, Long> portfolio = new HashMap<>();

    public ClientObject(final String name, final double funds) {
        this.name = name;
        this.funds = funds;
    }

    public void changeFunds(final double delta) {
        if (funds + delta < 0)  throw new IllegalArgumentException(
                String.format("%s : Not enough money for operation, has: %f, want to spend: %f", name, funds, delta));
        this.funds += delta;
    }

    public void changePortfolio(final String companyName, final long quantityDelta){
        if (quantityDelta > 0) {
            if (portfolio.containsKey(companyName)){
                portfolio.put(companyName, portfolio.get(companyName) + quantityDelta);
            } else {
                portfolio.put(companyName, quantityDelta);
            }
        } else if (quantityDelta < 0) {
            if (portfolio.containsKey(companyName)){
                if (portfolio.get(companyName) < -quantityDelta) {
                    throw new IllegalArgumentException(
                            String.format("%s : Not enough stocks for company %s, has: %d, want to sell %d",
                                    name, companyName, portfolio.get(companyName), -quantityDelta));
                }
                portfolio.put(companyName, portfolio.get(companyName) + quantityDelta);
            } else {
                throw new IllegalArgumentException(
                        String.format("%s : Not enough stocks for company %s", name,  companyName));
            }
        }
    }
}
