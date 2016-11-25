package com.mk.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mk.entity.Stock;
import com.mk.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;

/**
 * Created by mk on 11/24/16.
 */
public class PriceUpdaterTimerTask extends TimerTask {

    private static final Logger logger = LoggerFactory.getLogger(PriceUpdaterTimerTask.class);

    private WebsocketBroadcastHandler broadcastHandler;
    private StockService stockService;

    private static final List<String> TICKERS = Arrays.asList("AMD", "AAPL", "INTC");

    public PriceUpdaterTimerTask(WebsocketBroadcastHandler broadcastHandler, StockService stockService) {
        this.broadcastHandler = broadcastHandler;
        this.stockService = stockService;
    }

    @Override
    public void run() {
        try {
            logger.debug("UPDATING PRICES NOW");
            List<Stock> stocks = stockService.getPrices(TICKERS);
            logger.debug("FINISHED GETTING PRICES");
            logger.debug("BROADCASTING");
            ObjectMapper objectMapper = new ObjectMapper();
            broadcastHandler.broadcast(objectMapper.writeValueAsString(stocks));
            logger.debug("FINISHED BROADCASTING");
        } catch (JsonProcessingException e) {
            logger.error("Error in sending prices", e);
        }
    }
}
