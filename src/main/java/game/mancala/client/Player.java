package game.mancala.client;


import org.springframework.web.socket.WebSocketSession;


public class Player {


    public static Player createPlayer(WebSocketSession session) {
        return new Player(session);
    }

    private final WebSocketSession session;

    private int iteration[];
    private int offset;

    public Integer convertBoardIndex(Integer index) {
        return index + offset;
    }

    public Integer getNext(Integer index) {
        return iteration[index];
    }

    public String getLastStatus(int[] board) {
        String ret = "";
        Integer next = offset;
        Integer last = 0;
        int i = 0;
        while (i < board.length - 1) {
            ret = ret + board[next] + ",";
            last = next;
            next = getNext(next);
            i++;
        }
        ret = ret + board[last + 1];
        return ret;
    }


    public void setIteration(int[] iteration) {
        this.iteration = iteration;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    private Player(WebSocketSession session) {
        this.session = session;
    }

    public WebSocketSession getSession() {
        return session;
    }


}
