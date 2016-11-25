package com.mk.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mk on 11/11/16.
 */
public class WebsocketServerInitializer extends ChannelInitializer<SocketChannel> {

    private static final Logger logger = LoggerFactory.getLogger(WebsocketServerInitializer.class);

    private static final String WEBSOCKET_PATH = "/ticker";
    private final SslContext sslCtx;
    private final WebsocketBroadcastHandler broadcastHandler;


    public WebsocketServerInitializer(SslContext sslCtx, WebsocketBroadcastHandler broadcastHandler) {
        this.sslCtx = sslCtx;
        this.broadcastHandler = broadcastHandler;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH, null, true));
        pipeline.addLast(new WebsocketInputFrameHandler(broadcastHandler));
    }


}
