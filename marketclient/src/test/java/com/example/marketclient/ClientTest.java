package com.example.marketclient;

import com.example.marketclient.model.ClientModel;
import com.example.marketclient.model.ClientObject;
import com.example.marketclient.model.InMemoryClientModel;
import com.example.marketclient.service.RestService;
import com.example.marketclient.service.StockService;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Random;

public class ClientTest extends BaseTest{
    private static ClientModel clientModel;
    private static RestService restService;

    private static String newRandomName() {
        byte[] array = new byte[16];
        new Random().nextBytes(array);
        return new String(array, StandardCharsets.UTF_8);
    }

    @BeforeClass
    public static void prepareClientModel() {
        System.out.println("before class");
        restService = new RestService(new RestTemplateBuilder());
        restService.serverPort = stocksServerContainer.getMappedPort(8080);
        clientModel = new InMemoryClientModel(new StockService(restService));
        System.out.println("after before class");
    }

    private static ClientObject newClient(final String name, final double funds) {
        clientModel.addClient(new ClientObject(name, funds));
        final ClientObject client = clientModel.getClient(name);
        Assert.assertEquals(client.getName(), name);
        Assert.assertEquals(client.getFunds(), funds, 0.0);
        Assert.assertEquals(client.getPortfolio(), Map.of());
        return client;
    }

    @Test
    public void basic() {
        System.out.println("kek");
    }

    @Test
    public void newUserTest() {
        newClient("kek", 10.0);
    }

    @Test
    public void testNewClientAlreadyExists() {
        final String name = newRandomName();
        newClient(name, 10.0);
        Assert.assertThrows(IllegalArgumentException.class, () -> newClient(name, 200.0));
    }

    @Test
    public void testAddFunds() {
        final String name = newRandomName();
        newClient(name, 50.0);
        clientModel.addFunds(name, 200.0);
        Assert.assertEquals(250.0, clientModel.getClient(name).getFunds(), 0.0);
    }

    @Test
    public void pingTest(){
        System.out.println(restService.pingStock());
    }

    @Test
    public void testBuyOrSellStockOnce() {
        final String name = newRandomName();
        final ClientObject client = newClient(name, 15000.0);

        clientModel.buyOrSell(name, "yandex", 10);
        Assert.assertEquals(1, client.getPortfolio().size());
        Assert.assertEquals((Long) (long) 10, client.getPortfolio().get("yandex"));
        Assert.assertEquals(0.0, client.getFunds(), 1.0);
        Assert.assertEquals(15000.0, clientModel.totalValue(name), 1.0);
    }

    @Test
    public void testBuyOrSellStocks() {
        final String name = newRandomName();
        final ClientObject client = newClient(name, 40000.0);

        clientModel.buyOrSell(name,  "google", 10);
        Assert.assertEquals(1, client.getPortfolio().size());
        Assert.assertEquals((Long) (long) 10, client.getPortfolio().get("google"));
        Assert.assertEquals(30000.0, client.getFunds(), 1.0);
        Assert.assertEquals(40000.0, clientModel.totalValue(name), 1.0);

        clientModel.buyOrSell(name, "google", -3);
        Assert.assertEquals(1, client.getPortfolio().size());
        Assert.assertEquals((Long) (long) 7, client.getPortfolio().get("google"));
        Assert.assertEquals(33000.0, client.getFunds(), 5.0);
        Assert.assertEquals(40000.0, clientModel.totalValue(name), 5.0);

        clientModel.buyOrSell(name, "google", -7);
        Assert.assertEquals((Long) (long) 0, client.getPortfolio().get("google"));
        Assert.assertEquals(40000.0, client.getFunds(), 5.0);
        Assert.assertEquals(40000.0, clientModel.totalValue(name), 5.0);
    }

    @Test
    public void testBuyStocksNotEnoughStocksInMarket() {
        final String name = newRandomName();
        final ClientObject client = newClient(name, 10000000.0);
        Assert.assertThrows(HttpClientErrorException.BadRequest.class,
                () -> clientModel.buyOrSell(name, "google", 1000));
        Assert.assertEquals(10000000.0, client.getFunds(), 0.0);
    }

    @Test
    public void testBuyStocksNotEnoughStocksInClient() {
        final String name = newRandomName();
        final ClientObject client = newClient(name, 100.0);
        Assert.assertThrows(IllegalArgumentException.class,
                () -> clientModel.buyOrSell(name, "google", -1000));
        Assert.assertEquals(100.0, client.getFunds(), 0.0);
    }

    @Test
    public void testBuyStocksNotEnoughFunds() {
        final String name = newRandomName();
        final ClientObject client = newClient(name, 0.0);
        Assert.assertThrows(IllegalArgumentException.class,
                () -> clientModel.buyOrSell(name, "google", 1));
        Assert.assertEquals(0.0, client.getFunds(), 0.0);
    }

//    @Test
//    public void withDice() throws Exception {
//        final String name = newRandomName();
//        final ClientObject client = newClient(name, 4000.0);
//
//        clientModel.buyOrSell(name,  "intel", 3);
//        rollTheDice();
//        double after = clientModel.totalValue(name);
//        System.out.println(after);
//        Assert.assertNotEquals(4000.0, after);
//
//    }
}
