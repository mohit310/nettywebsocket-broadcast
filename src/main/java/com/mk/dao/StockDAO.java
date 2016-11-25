package com.mk.dao;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mk.entity.Stock;

import java.io.IOException;
import java.net.URL;

/**
 * Created by mk on 11/24/16.
 */
public class StockDAO {

    private static final String STOCK_URL = "https://www.google.com/finance/info?q=";

    public Stock getPrice(String ticker) throws RuntimeException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        Stock stock = null;
        try {
            URL url = new URL(STOCK_URL + ticker);
            stock = mapper.readValue(url, Stock.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return stock;
    }
}
