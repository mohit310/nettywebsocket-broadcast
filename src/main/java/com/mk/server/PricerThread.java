package com.mk.server;

import com.mk.dao.StockDAO;
import com.mk.service.StockService;

import java.util.Timer;

/**
 * Created by mk on 11/24/16.
 */
public class PricerThread implements Runnable {

    private final StockService stockService = new StockService(new StockDAO());
    private final WebsocketBroadcastHandler broadcastHandler;

    public PricerThread(WebsocketBroadcastHandler broadcastHandler) {
        this.broadcastHandler = broadcastHandler;
    }

    @Override
    public void run() {
        Timer timer = new Timer();
        timer.schedule(new PriceUpdaterTimerTask(broadcastHandler, stockService), 15000l, 15000l);
    }
}
