package ru.zhelobkovich.sd.stockmarket.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.zhelobkovich.sd.stockmarket.model.CompanyRepository;
import ru.zhelobkovich.sd.stockmarket.model.InMemoryCompanyModel;
import ru.zhelobkovich.sd.stockmarket.model.InMemoryStockModel;
import ru.zhelobkovich.sd.stockmarket.model.StockRepository;

@Configuration
public class SpringConfig {

    @Bean
    CompanyRepository companyDao(){
        return new InMemoryCompanyModel();
    }

    @Bean
    StockRepository stockDatastore(){
        return new InMemoryStockModel();
    }
}
