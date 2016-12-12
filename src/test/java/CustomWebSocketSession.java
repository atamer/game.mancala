import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.adapter.standard.StandardWebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


class CustomWebSocketSession extends StandardWebSocketSession {

    private final List<String> messageList = new ArrayList<>();

    public CustomWebSocketSession(HttpHeaders headers, Map<String, Object> attributes) {

        super(headers, attributes, null, null, null);
    }

    @Override
    protected void sendTextMessage(TextMessage message) throws IOException {
        messageList.add(message.getPayload());
    }

    public List<String> getMessageList() {
        return messageList;
    }
}
