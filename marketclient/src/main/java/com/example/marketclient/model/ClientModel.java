package com.example.marketclient.model;

public interface ClientModel {
    void addClient(final ClientObject client);

    ClientObject getClient(final String name);

    void addFunds(final String name, final double delta);

    boolean hasStock(final String name,  final String companyName, final long quantity);

    void buyOrSell(final String name, final String companyName, final long quantity);

    double totalValue(final String name);

    double queryPrice(final String stockName);
}
