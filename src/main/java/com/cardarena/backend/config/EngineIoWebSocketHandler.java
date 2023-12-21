//package com.cardarena.backend.config;
//
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.*;
//
//import javax.websocket.CloseReason;
//import javax.websocket.Session;
//
//@Component
//public class EngineIoWebSocketHandler implements WebSocketHandler {
//
//    private final EngineIoServer mEngineIoServer;
//
//    public EngineIoWebSocketHandler(EngineIoEndpoint engineIoEndpoint) {
//        this.engineIoEndpoint = engineIoEndpoint;
//    }
//
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        final EngineIoSpringWebSocket webSocket = new EngineIoSpringWebSocket(session);
//        webSocketSession.getAttributes().put(ATTRIBUTE_ENGINEIO_BRIDGE, webSocket);
//        mEngineIoServer.handleWebSocket(webSocket);
//    }
//
//    @Override
//    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
//        engineIoEndpoint.onMessage((String) message.getPayload(), (Session) session);
//    }
//
//    @Override
//    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
//        engineIoEndpoint.onError((Session) session, exception);
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
//        CloseReason closeReason = new CloseReason(CloseReason.CloseCodes.getCloseCode(closeStatus.getCode()), closeStatus.getReason());
//        engineIoEndpoint.onClose((Session) session, closeReason);
//    }
//
//    @Override
//    public boolean supportsPartialMessages() {
//        return false;
//    }
//}
