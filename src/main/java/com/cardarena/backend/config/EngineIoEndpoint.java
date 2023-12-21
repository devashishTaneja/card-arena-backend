//package com.cardarena.backend.config;
//
//import io.socket.engineio.server.EngineIoServer;
//import io.socket.engineio.server.EngineIoWebSocket;
//import io.socket.engineio.server.utils.ParseQS;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.server.standard.SpringConfigurator;
//import javax.websocket.*;
//import javax.websocket.server.ServerEndpoint;
//import java.io.IOException;
//import java.nio.ByteBuffer;
//import java.util.List;
//import java.util.Map;
//
//@Component
//@ServerEndpoint(value = "/engine.io/*", configurator = SpringConfigurator.class)
//public class EngineIoEndpoint {
//    private Session mSession;
//    private Map<String, String> mQuery;
//    private EngineIoWebSocket mEngineIoWebSocket;
//    @Autowired
//    private EngineIoServer mEngineIoServer;
//
//    @OnOpen
//    public void onOpen(Session session) {
//        // Handle the WebSocket connection being opened
//        mSession = session;
//        mQuery = ParseQS.decode(session.getQueryString());
//
//        mEngineIoWebSocket = new EngineIoWebSocketImpl();
//
//        /*
//         * These cannot be converted to lambda because of runtime type inference
//         * by server.
//         */
//        mSession.addMessageHandler(new MessageHandler.Whole<String>() {
//            @Override
//            public void onMessage(String message) {
//                mEngineIoWebSocket.emit("message", message);
//            }
//        });
//        mSession.addMessageHandler(new MessageHandler.Whole<byte[]>() {
//            @Override
//            public void onMessage(byte[] message) {
//                mEngineIoWebSocket.emit("message", (Object)message);
//            }
//        });
//
//        mEngineIoServer.handleWebSocket(mEngineIoWebSocket);
//
//    }
//
//    @OnMessage
//    public void onMessage(String message, Session session) {
//        // Handle incoming WebSocket messages
//    }
//
//    @OnClose
//    public void onClose(Session session, CloseReason closeReason) {
//        // Handle the WebSocket connection being closed
//    }
//
//    @OnError
//    public void onError(Session session, Throwable throwable) {
//        // Handle WebSocket errors
//    }
//
//
//    private class EngineIoWebSocketImpl extends EngineIoWebSocket {
//
//        private RemoteEndpoint.Basic mBasic;
//
//        EngineIoWebSocketImpl() {
//            mBasic = mSession.getBasicRemote();
//        }
//
//        @Override
//        public Map<String, String> getQuery() {
//            return mQuery;
//        }
//
//        @Override
//        public Map<String, List<String>> getConnectionHeaders() {
//            return Map.of();
//        }
//
//        @Override
//        public void write(String message) throws IOException {
//            mBasic.sendText(message);
//        }
//
//        @Override
//        public void write(byte[] message) throws IOException {
//            mBasic.sendBinary(ByteBuffer.wrap(message));
//        }
//
//        @Override
//        public void close() {
//            try {
//                mSession.close();
//            } catch (IOException ignore) {
//            }
//        }
//    }
//}
//
//
