package ru.zhelobkovich.sd.stockmarket.model;

import org.springframework.cglib.core.internal.Function;

import java.util.List;

public interface StockRepository {
    boolean addStock(StockObject stock);

    double getPrice(String name);

    void modifyStocks(Function<StockObject, StockObject> function);

    List<StockObject> getAllStocks();

    long modifyStock(final String companyName, final long quantityDelta);
}
