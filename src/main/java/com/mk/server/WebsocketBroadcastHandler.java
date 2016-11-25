package com.mk.server;

import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by mk on 11/24/16.
 */
public class WebsocketBroadcastHandler {

    private static final Logger logger = LoggerFactory.getLogger(WebsocketBroadcastHandler.class);

    private static final List<SocketChannel> channels = new ArrayList<>();

    public synchronized void addSocketChannel(SocketChannel socketChannel) {
        if (!channels.contains(socketChannel)) channels.add(socketChannel);
    }

    public synchronized void broadcast(String message) {
        logger.debug("Broadcasting now");
        for (SocketChannel socketChannel : channels) {
            if (socketChannel.isOpen() && socketChannel.isWritable()) {
                socketChannel.writeAndFlush(new TextWebSocketFrame(message));
            }
        }
        logger.info("Total active sockets {}", channels.size());
    }

    public synchronized void removeSocketChannel(SocketChannel channel) {
        logger.debug("REMOVING channel: " + channel.toString());
        channels.remove(channel);
    }
}
