package ru.zhelobkovich.sd.stockmarket.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StockObject {
    private final String companyName;
    private long stockCount;
    private double stockPrice;
}
