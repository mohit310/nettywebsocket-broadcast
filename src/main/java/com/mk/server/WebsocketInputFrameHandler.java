package com.mk.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;


/**
 * Created by mk on 11/11/16.
 */
public class WebsocketInputFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private static final Logger logger = LoggerFactory.getLogger(WebsocketInputFrameHandler.class);

    private WebsocketBroadcastHandler websocketBroadcastHandler;

    public WebsocketInputFrameHandler(WebsocketBroadcastHandler websocketBroadcastHandler) {
        this.websocketBroadcastHandler = websocketBroadcastHandler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        if (frame instanceof TextWebSocketFrame) {
            String request = ((TextWebSocketFrame) frame).text();
            logger.info("{} received {}", ctx.channel(), request);
        } else {
            String message = "unsupported frame type: " + frame.getClass().getName();
            throw new UnsupportedOperationException(message);
        }
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            websocketBroadcastHandler.addSocketChannel((SocketChannel) ctx.channel());
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        websocketBroadcastHandler.removeSocketChannel((SocketChannel) ctx.channel());
        super.channelUnregistered(ctx);
    }
}
