package com.example.marketclient;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BaseTest {
    protected static class Stock {
        final String name;
        final double price;

        private Stock(String name, double price) {
            this.name = name;
            this.price = price;
        }
    }

    @ClassRule
    public static GenericContainer stocksServerContainer
            = new FixedHostPortGenericContainer("stockmarket:0.0.1-SNAPSHOT")
            .withFixedExposedPort(8080, 8080)
            .withExposedPorts(8080);

    protected static final List<String> companyNames = List.of("google", "yandex", "intel");

    protected static final List<Stock> stocks = new ArrayList<>();

    static {
        stocks.add(new Stock( "google", 1000.0));
        stocks.add(new Stock( "yandex", 1500.0));
        stocks.add(new Stock( "intel", 100.0));
    }

    @BeforeClass
    public static void fillMarket() throws Exception {
        for (final String companyName : companyNames) {
            createCompany(companyName);
        }
        for (Stock stock : stocks) {
            createStock(stock.name, stock.price, 10);
        }
    }

    private static URI buildStocksServerUri(Function<UriComponentsBuilder, UriComponentsBuilder> uriModifier) {
        return uriModifier.apply(UriComponentsBuilder.newInstance())
                .scheme("http")
                .host(stocksServerContainer.getHost())
                .port(stocksServerContainer.getMappedPort(8080))
                .build()
                .toUri();
    }

    private static void createCompany(String name) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(buildStocksServerUri(builder -> builder
                        .path("/new-company")
                        .queryParam("name", name)
                ))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        System.out.println("CC response is: " +
                HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString()));
    }

    private static void createStock(String name, double price, long amount) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(buildStocksServerUri(builder -> builder
                        .path("/new-stock")
                        .queryParam("company", name)
                        .queryParam("quantity", amount)
                        .queryParam("price", price)
                ))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        System.out.println("CStock response is: " +
                HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString()));
    }

    protected static void rollTheDice() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(buildStocksServerUri(builder -> builder
                        .path("/roll-the-dice")
                        .queryParam("percentage", 10)
                ))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        System.out.println("Roll the dice response is: " +
                HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    public void containerTest() throws IOException, InterruptedException {
        stocksServerContainer.execInContainer("touch", "/somefile.txt");
        Container.ExecResult lsResult = stocksServerContainer.execInContainer("ls", "-al", "/");
        String stdout = lsResult.getStdout();
        int exitCode = lsResult.getExitCode();
        assertTrue(stdout.contains("somefile.txt"));
        assertEquals(0, exitCode);
    }
}
