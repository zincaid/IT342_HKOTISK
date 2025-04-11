// src/main/java/com/CSIT321/Hkotisk/Handler/OrderWebSocketHandler.java
package com.CSIT321.Hkotisk.Handler;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = Logger.getLogger(OrderWebSocketHandler.class.getName());
    private static final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        try {
            session.sendMessage(new TextMessage("Connection established"));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error sending message", e);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            session.sendMessage(new TextMessage("Order received: " + message.getPayload()));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error sending message", e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        logger.info("Connection closed: " + session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.log(Level.SEVERE, "Transport error: " + session.getId(), exception);
        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR);
        }
        sessions.remove(session);
    }

    public static void sendMessageToAll(String message) throws Exception {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Error sending message to session: " + session.getId(), e);
                }
            }
        }
    }
}