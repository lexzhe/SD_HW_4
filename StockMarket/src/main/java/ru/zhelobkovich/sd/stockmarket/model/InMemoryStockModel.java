package ru.zhelobkovich.sd.stockmarket.model;

import org.springframework.cglib.core.internal.Function;

import java.util.*;


public class InMemoryStockModel implements StockRepository {
    private final Map<String, StockObject> stocksByCompanyName = new HashMap<>();

    @Override
    public boolean addStock(final StockObject stock) {
        final String companyName = stock.getCompanyName();
        if (stocksByCompanyName.containsKey(companyName)) {
            throw new IllegalArgumentException(
                    "Stock for '" + stock.getCompanyName() + "' already exist."
            );
        } else {
            stocksByCompanyName.put(companyName,stock);
            return true;
        }
    }

    @Override
    public List<StockObject> getAllStocks() {
        return new ArrayList<StockObject>(stocksByCompanyName.values());
    }

    @Override
    public double getPrice(String name) {
        StockObject stock = stocksByCompanyName.get(name);
        if (stock == null) throw new IllegalArgumentException("No stock: " + name);
        return stock.getStockPrice();
    }

    @Override
    public void modifyStocks(Function<StockObject, StockObject> function) {
        stocksByCompanyName.entrySet().forEach(stringStockObjectEntry ->
                stringStockObjectEntry.setValue(function.apply(stringStockObjectEntry.getValue())));
    }

    @Override
    public long modifyStock(final String companyName, final long quantityDelta) {
        StockObject stock = stocksByCompanyName.get(companyName);
        if (stock == null) {
            throw new IllegalArgumentException("Stock '" + companyName + "' is not present.");
        }
        final long newCount = stock.getStockCount() + quantityDelta;
        if (newCount < 0) {
            throw new IllegalArgumentException("New stock's quantity will be " + newCount + ", impossible.");
        }
        stock.setStockCount(newCount);
        return newCount;
    }
}
