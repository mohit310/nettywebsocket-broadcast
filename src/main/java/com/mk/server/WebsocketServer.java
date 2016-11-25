package com.mk.server;

import com.mk.dao.StockDAO;
import com.mk.service.StockService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import java.util.Timer;


/**
 * Created by mk on 11/11/16.
 */
public class WebsocketServer {

    private static final boolean SSL = System.getProperty("ssl") != null;
    private static final int PORT = Integer.parseInt(System.getProperty("port", SSL ? "8443" : "8080"));

    private final WebsocketBroadcastHandler websocketBroadcastHandler = new WebsocketBroadcastHandler();

    public static void main(String[] args) throws Exception {
        WebsocketServer server = new WebsocketServer();
        server.start();
    }

    public void start() throws Exception {

        PricerThread pricerRunnable = new PricerThread(websocketBroadcastHandler);
        Thread pricerThread = new Thread(pricerRunnable);
        pricerThread.start();

        // Configure SSL.
        final SslContext sslCtx;
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }
        EventLoopGroup bossGroup = new NioEventLoopGroup(10);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new WebsocketServerInitializer(sslCtx, websocketBroadcastHandler));

            Channel ch = b.bind(PORT).sync().channel();

            System.out.println("Open your web browser and navigate to " +
                    (SSL ? "https" : "http") + "://127.0.0.1:" + PORT + '/');

            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

        pricerThread.join();
    }
}
