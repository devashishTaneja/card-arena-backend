//package com.cardarena.backend.config;
//
//import io.socket.engineio.server.EngineIoWebSocket;
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.web.socket.WebSocketMessage;
//
//import java.io.IOException;
//
//public class EngineIoSpringWebSocket extends EngineIoWebSocket {
//    private final WebSocketSession mSession;
//    private final Map<String, String> mQuery;
//    private final Map<String, List<String>> mHeaders;
//
//    EngineIoSpringWebSocket(WebSocketSession session) {
//        mSession = session;
//
//        final String queryString = (String)mSession.getAttributes().get(ATTRIBUTE_ENGINEIO_QUERY);
//        if (queryString != null) {
//            mQuery = ParseQS.decode(queryString);
//        } else {
//            mQuery = new HashMap<>();
//        }
//        this.mHeaders = (Map<String, List<String>>) mSession.getAttributes().get(ATTRIBUTE_ENGINEIO_HEADERS);
//    }
//
//    /* EngineIoWebSocket */
//
//    @Override
//    public Map<String, String> getQuery() {
//        return mQuery;
//    }
//
//    @Override
//    public Map<String, List<String>> getConnectionHeaders() {
//        return mHeaders;
//    }
//
//    @Override
//    public void write(String message) throws IOException {
//        mSession.sendMessage(new TextMessage(message));
//    }
//
//    @Override
//    public void write(byte[] message) throws IOException {
//        mSession.sendMessage(new BinaryMessage(message));
//    }
//
//    @Override
//    public void close() {
//        try {
//            mSession.close();
//        } catch (IOException ignore) {
//        }
//    }
//
//    /* WebSocketHandler */
//
//    void afterConnectionClosed(CloseStatus closeStatus) {
//        emit("close");
//    }
//
//    void handleMessage(WebSocketMessage<?> message) {
//        if (message.getPayload() instanceof String || message.getPayload() instanceof byte[]) {
//            emit("message", (Object) message.getPayload());
//        } else {
//            throw new RuntimeException(String.format(
//                "Invalid message type received: %s. Expected String or byte[].",
//                message.getPayload().getClass().getName()));
//        }
//    }
//
//    void handleTransportError(Throwable exception) {
//        emit("error", "write error", exception.getMessage());
//    }
//}
