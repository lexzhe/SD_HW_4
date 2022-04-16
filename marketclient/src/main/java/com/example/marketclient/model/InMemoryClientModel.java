package com.example.marketclient.model;

import com.example.marketclient.service.StockService;

import java.util.HashMap;
import java.util.Map;

public class InMemoryClientModel implements ClientModel {
    private final Map<String, ClientObject> clientByName = new HashMap<>();
    private final StockService stockService;

    public InMemoryClientModel(final StockService stockService) {
        this.stockService = stockService;
    }

    private void checkContains(final String name) {
        if (!this.clientByName.containsKey(name)) {
            throw new IllegalArgumentException("Client '" + name + "' does not exist.");
        }
    }

    @Override
    synchronized public void addClient(final ClientObject client) {
        if (this.clientByName.containsKey(client.getName())) {
            throw new IllegalArgumentException("Client '" + client.getName() + "' already exists.");
        }
        this.clientByName.put(client.getName(), client);
    }

    @Override
    synchronized public ClientObject getClient(final String name) {
        checkContains(name);
        return this.clientByName.get(name);
    }

    @Override
    synchronized public void addFunds(final String name, final double delta) {
        checkContains(name);
        this.clientByName.get(name).changeFunds(delta);
    }

    @Override
    synchronized public boolean hasStock(final String name, final String companyName, final long quantity) {
        return getClient(name).getPortfolio().entrySet().stream()
                .filter(stock -> stock.getKey().equals(companyName))
                .mapToLong(Map.Entry::getValue)
                .findFirst().orElse(0)
                >= quantity;
    }

    @Override
    synchronized public void buyOrSell(final String name, final String companyName, final long quantityDelta) {
        if (quantityDelta < 0 && !this.hasStock(name, companyName, -quantityDelta)) {
            throw new IllegalArgumentException("Not enough stocks for selling.");
        }
        final double cost = stockService.queryPrice(companyName);
        final ClientObject client = this.getClient(name);
        if (client.getFunds() < cost * quantityDelta) {
            throw new IllegalArgumentException("Not enough money: " + client.getFunds() + " < " + cost * quantityDelta);
        }
        stockService.modifyStock(companyName, -quantityDelta);
        client.changeFunds(-cost * quantityDelta);
        client.changePortfolio(companyName, quantityDelta);
    }

    @Override
    public double totalValue(final String name) {
        final ClientObject client = this.getClient(name);
        return client.getFunds() + client.getPortfolio().entrySet().stream()
                .mapToDouble(stock -> stock.getValue() * this.stockService.queryPrice(stock.getKey()))
                .sum();
    }

    @Override
    public double queryPrice(final String companyName) {
        return this.stockService.queryPrice(companyName);
    }
}
