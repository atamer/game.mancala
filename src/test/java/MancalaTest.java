import game.mancala.Application;
import game.mancala.client.Game;
import game.mancala.client.GameEngine;
import game.mancala.client.Player;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;

import javax.websocket.Session;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class MancalaTest {

    private final HttpHeaders headers = new HttpHeaders();

    private final Map<String, Object> attributes = new HashMap<>();


    @Test
    public void joinTest() throws IOException {

        Session nativeSession = Mockito.mock(Session.class);

        CustomWebSocketSession session1 = new CustomWebSocketSession(this.headers, this.attributes);
        session1.initializeNativeSession(nativeSession);
        Player p1 = Player.createPlayer(session1);

        CustomWebSocketSession session2 = new CustomWebSocketSession(this.headers, this.attributes);
        session2.initializeNativeSession(nativeSession);
        Player p2 = Player.createPlayer(session2);

        GameEngine.instance().checkAndCreateNewGame(p1);
        GameEngine.instance().checkAndCreateNewGame(p2);


        String s1 = session1.getMessageList().stream().collect(Collectors.joining(","));
        String s2 = session2.getMessageList().stream().collect(Collectors.joining(","));
        Assert.assertEquals(s1, "{'command':'start'},{'command':'yourturn'}");
        Assert.assertEquals(s2, "{'command':'start'}");


    }


    @Test
    public void movementTest() throws IOException {

        Session nativeSession = Mockito.mock(Session.class);

        CustomWebSocketSession session1 = new CustomWebSocketSession(this.headers, this.attributes);
        session1.initializeNativeSession(nativeSession);
        Player p1 = Player.createPlayer(session1);

        CustomWebSocketSession session2 = new CustomWebSocketSession(this.headers, this.attributes);
        session2.initializeNativeSession(nativeSession);
        Player p2 = Player.createPlayer(session2);

        Game game = GameEngine.instance().checkAndCreateNewGame(p1);
        GameEngine.instance().checkAndCreateNewGame(p2);


        game.nextMove(p1, 0);

        String s1 = session1.getMessageList().stream().collect(Collectors.joining(","));
        String s2 = session2.getMessageList().stream().collect(Collectors.joining(","));
        Assert.assertEquals(s1, "{'command':'start'},{'command':'yourturn'},{'lastStatus':'0,7,7,7,7,7,1,6,6,6,6,6,6,0'},{'command':'yourturn'}");
        Assert.assertEquals(s2, "{'command':'start'},{'lastStatus':'6,6,6,6,6,6,0,0,7,7,7,7,7,1'},{'command':'nextturn'}");

    }

}
