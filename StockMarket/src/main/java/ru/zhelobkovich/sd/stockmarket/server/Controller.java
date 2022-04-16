package ru.zhelobkovich.sd.stockmarket.server;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.zhelobkovich.sd.stockmarket.model.CompanyRepository;
import ru.zhelobkovich.sd.stockmarket.model.CompanyObject;
import ru.zhelobkovich.sd.stockmarket.model.StockRepository;
import ru.zhelobkovich.sd.stockmarket.model.StockObject;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@RestController
public class Controller {
    private final CompanyRepository companyRepository;

    private final StockRepository stockRepository;

    public Controller(final CompanyRepository companyRepository, final StockRepository stockRepository) {
        this.companyRepository = companyRepository;
        this.stockRepository = stockRepository;
    }

    private ResponseEntity<?> execute(final Callable<String> callable) {
        try {
            return new ResponseEntity<>(callable.call() + System.lineSeparator(), HttpStatus.OK);
        } catch (final IllegalArgumentException e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage() + System.lineSeparator(), HttpStatus.BAD_REQUEST);
        } catch (final Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/new-company")
    public ResponseEntity<?> newCompany(@RequestParam("name") final String name) {
        return execute(() -> {
            companyRepository.addCompany(new CompanyObject(name));
            return "Company '" + name + "' has been successfully added.";
        });
    }

    @RequestMapping("/new-stock")
    public ResponseEntity<?> newStock(@RequestParam("company") final String companyName,
                                      @RequestParam("price") final double price,
                                      @RequestParam("quantity") final long quantity) {
        return execute(() -> {
            this.stockRepository.addStock(new StockObject(companyName, quantity, price));
            return "New stock '" + companyName + "' has been successfully added.";
        });
    }

    @RequestMapping("/stock-info")
    public ResponseEntity<?> stockInfo() {
        return execute(() ->
                this.stockRepository.getAllStocks().stream()
                        .map(stock -> "'" + stock.getCompanyName()
                                + "', quantity: " + stock.getStockCount() + ", price: " + stock.getStockPrice())
                        .collect(Collectors.joining("|"))
        );
    }

    @RequestMapping("/roll-the-dice")
    public ResponseEntity<?> modifyStock(@RequestParam("percentage") final int percentage) {
        return execute(() -> {
            this.stockRepository.modifyStocks(stock -> {
                stock.setStockPrice(rollTheDiceOnce(stock.getStockPrice(), percentage));
                return stock;
            });
            return "The Dice were rolled!!!";
        });
    }

    @RequestMapping("/modify-stock")
    public ResponseEntity<?> modifyStock(@RequestParam("name") final String companyName,
                                         @RequestParam("count") final long quantityDelta) {
        return execute(() -> {
            long count = this.stockRepository.modifyStock(companyName, quantityDelta);
            return "Successfully modified stock '" + companyName + "', count: " + count;
        });
    }

    @RequestMapping("/get-stock-price")
    public ResponseEntity<?> getStockPrice(@RequestParam("name") final String companyName) {
        return execute(() -> Double.toString(this.stockRepository.getPrice(companyName)));
    }

    @RequestMapping("/ping")
    public ResponseEntity<?> ping() {
        return new ResponseEntity<>("pong", HttpStatus.OK);
    }

    private double rollTheDiceOnce(double currentPrice, int percentage) {
        Random random = new Random();
        return currentPrice + currentPrice * ((double) percentage / 100) * (1 - 2 * random.nextDouble());
    }
}
