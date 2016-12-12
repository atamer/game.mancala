package game.mancala.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


public class GameSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(GameSocketHandler.class);

    private Game game;
    private Player player;


    public GameSocketHandler() {


    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        try {
            player = Player.createPlayer(session);
            game = GameEngine.instance().checkAndCreateNewGame(player);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws Exception {
        String messageStr = message.getPayload();
        if (messageStr != null) {
            String messageArray[] = messageStr.split(":");
            if (messageArray.length == 2) {
                if (messageArray[0].equals("click")) {
                    game.nextMove(player, Integer.valueOf(messageArray[1]));
                }
            }
        }


    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception)
            throws Exception {
        session.close(CloseStatus.SERVER_ERROR);
    }

}
