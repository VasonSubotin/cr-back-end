package com.sm.ocpp;

import eu.chargetime.ocpp.*;
import eu.chargetime.ocpp.model.SessionInformation;
import eu.chargetime.ocpp.wss.WssFactoryBuilder;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.util.Map;

public class SmWebSocketListener extends WebSocketListener {
    private Map<String, Object> credentials;

    public SmWebSocketListener(ISessionFactory sessionFactory, JSONConfiguration configuration, Map<String, Object> credentials, Draft... drafts) {
        super(sessionFactory, configuration, drafts);
        this.credentials = credentials;
    }

    public void open(String hostname, int port, final ListenerEvents handler) {
        this.server = new WebSocketServer(new InetSocketAddress(hostname, port), this.drafts) {
            public void onOpen(final WebSocket webSocket, ClientHandshake clientHandshake) {
                WebSocketListener.logger.debug("On connection open (resource descriptor: {})", clientHandshake.getResourceDescriptor());
                WebSocketReceiver receiver = new WebSocketReceiver(new WebSocketReceiverEvents() {
                    public boolean isClosed() {
                        return SmWebSocketListener.this.closed;
                    }

                    public void close() {
                        webSocket.close();
                    }

                    public void relay(String message) {
                        webSocket.send(message);
                    }
                });
                SmWebSocketListener.this.sockets.put(webSocket, receiver);
                SessionInformation information = (new SessionInformation.Builder()).Identifier(clientHandshake.getResourceDescriptor()).InternetAddress(webSocket.getRemoteSocketAddress()).build();
                handler.newSession(SmWebSocketListener.this.sessionFactory.createSession(new JSONCommunicator(receiver)), information);
            }

            public void onClose(WebSocket webSocket, int code, String reason, boolean remote) {
                WebSocketListener.logger.debug("On connection close (resource descriptor: {}, code: {}, reason: {}, remote: {})", new Object[]{webSocket.getResourceDescriptor(), code, reason, remote});
                WebSocketReceiver receiver = (WebSocketReceiver) SmWebSocketListener.this.sockets.get(webSocket);
                if (receiver != null) {
                    receiver.disconnect();
                    SmWebSocketListener.this.sockets.remove(webSocket);
                } else {
                    WebSocketListener.logger.debug("Receiver for socket not found: {}", webSocket);
                }

            }

            public void onMessage(WebSocket webSocket, String message) {
                ((WebSocketReceiver) SmWebSocketListener.this.sockets.get(webSocket)).relay(message);
            }

            public void onError(WebSocket webSocket, Exception ex) {
                String resourceDescriptor = webSocket != null ? webSocket.getResourceDescriptor() : "not defined (webSocket is null)";
                if (ex instanceof ConnectException) {
                    WebSocketListener.logger.error("On error (resource descriptor: " + resourceDescriptor + ") triggered caused by:", ex);
                } else {
                    WebSocketListener.logger.error("On error (resource descriptor: " + resourceDescriptor + ") triggered:", ex);
                }

            }

            public void onStart() {
                WebSocketListener.logger.debug("Server socket bound");
            }
        };
        if (this.wssFactoryBuilder != null) {
            this.server.setWebSocketFactory(this.wssFactoryBuilder.build());
        }

        super.configure();
        this.server.start();
        this.closed = false;
    }

    protected void enableWSS(WssFactoryBuilder wssFactoryBuilder) {
        if (this.server != null) {
            throw new IllegalStateException("Cannot enable WSS on already running server");
        } else {
            this.wssFactoryBuilder = wssFactoryBuilder;
        }
    }

}
