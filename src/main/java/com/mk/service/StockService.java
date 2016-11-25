package com.mk.service;


import com.mk.dao.StockDAO;
import com.mk.entity.Stock;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by mk on 11/20/16.
 */
public class StockService {

    private StockDAO stockDAO;

    public StockService(StockDAO stockDAO) {
        this.stockDAO = stockDAO;
    }

    public Stock getPrice(String ticker) {
        return stockDAO.getPrice(ticker);
    }

    public List<Stock> getPrices(List<String> tickers) {
        return tickers.parallelStream().map(ticker -> stockDAO.getPrice(ticker)).collect(toList());
    }
}
