package com.example.marketclient;

import com.example.marketclient.model.ClientModel;
import com.example.marketclient.model.ClientObject;
import com.example.marketclient.service.RestService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static java.time.LocalTime.now;

@RestController
public class ClientController {

    private final RestService restService;

    private final ClientModel clientModel;

    public ClientController(final ClientModel clientModel, final RestService restService) {
        this.clientModel = clientModel;
        this.restService = restService;
    }

    @RequestMapping("/new-user")
    public String newUser(@RequestParam("name") final String name,
                          @RequestParam(name = "funds", required = false, defaultValue = "0") final double funds) {
        return execute(() -> {
            this.clientModel.addClient(new ClientObject(name, funds));
            return "Client '" + name + "' has been successfully added.";
        });
    }

    @RequestMapping("/add-funds")
    public String addFunds(@RequestParam("name") final String name, @RequestParam("delta") final double delta) {
        return execute(() -> {
            this.clientModel.addFunds(name, delta);
            return "Funds have been successfully added to '" + name + "'";
        });
    }

    @RequestMapping("/get-stocks")
    public String getStocksList(@RequestParam("name") final String name) {
        return execute(() ->
                this.clientModel.getClient(name).getPortfolio().entrySet().stream()
                        .map(stock -> stock.getKey() + " : " + stock.getValue() + " x " +
                                this.clientModel.queryPrice(stock.getKey()))
                        .collect(Collectors.joining(System.lineSeparator()))
        );
    }

    @RequestMapping("/get-total")
    public String getTotalValue(@RequestParam("name") final String name) {
        return execute(() -> name + "'s value is " + this.clientModel.totalValue(name));
    }

    @RequestMapping("/buy-sell")
    public String buyOrSell(@RequestParam("name") final String name,
                            @RequestParam("company-name") final String companyName,
                            @RequestParam("delta") final long delta) {
        return execute(() -> {
            if (delta < 0 && !this.clientModel.hasStock(name, companyName, -delta)) {
                return "Client cannot sell this stock." + System.lineSeparator();
            }
            this.clientModel.buyOrSell(name, companyName, delta);
            return name + " successfully " + ((delta>0)? "bought " : "sold ") + Math.abs(delta) + " units of '" + companyName + "'";
        });
    }

    @RequestMapping("/ping-stock")
    public String pingStock() {
        return execute(() -> restService.pingStock()? "Success" : "Failure");
    }

    private String execute(final Callable<String> response){
        try {
            return "["+ now().toString() +"]: " + response.call();
        } catch (final Throwable t){
            return "An error occurred " + t.getMessage();
        }
    }
}
