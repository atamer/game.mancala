package game.mancala.client;

import org.springframework.web.socket.TextMessage;

import java.io.IOException;


public class Game {

    public static final Integer BOARD_SIZE = 6;
    public static final Integer STONE_COUNT = 6;
    private int[] board;

    private Player player1;
    private Player player2;

    private Player turn;
    private boolean full;

    private Game() {

    }

    // factory method
    public static Game createNewGame() {
        return new Game();
    }

    public synchronized void addPlayer(Player p) {
        if (player1 == null) {
            player1 = p;
        } else {
            player2 = p;
            full = true;
        }

    }

    public void startGame() throws IOException {
        if (player1 == null || player2 == null) {
            throw new RuntimeException("one of player is null ");
        } else {
            // setup board
            int size = Game.BOARD_SIZE * 2 + 2;

            board = new int[size];
            for (int i = 0; i < size; i++) {
                if (i != Game.BOARD_SIZE && i != Game.BOARD_SIZE * 2 + 1) {
                    board[i] = Game.STONE_COUNT;
                }
            }
            turn = player1;
            sendMessage(0, "{'command':'start'}");
            sendMessage(1, "{'command':'yourturn'}");
        }
    }

    public void nextMove(Player p, Integer index) throws IOException {
        if (turn == p) {
            Integer boardIndex = p.convertBoardIndex(index);
            Integer stoneCount = board[boardIndex];
            board[boardIndex] = 0;
            Integer last = 0;

            Integer next = boardIndex;
            for (int i = 0; i < stoneCount; i++) {
                next = p.getNext(next);
                board[next] = board[next] + 1;
                last = next;
            }


            // check last location opposite
            // not home , not other side
            if (board[last] == 1 && (!last.equals(Game.BOARD_SIZE) && last != Game.BOARD_SIZE * 2 + 1) && ((last < p.getOffset() + Game.BOARD_SIZE) && (last >= p.getOffset()))) {
                // take the opposite
                int oppositeIndex = Game.BOARD_SIZE - (last - Game.BOARD_SIZE);
                int totalCount = board[oppositeIndex] + board[last];
                board[oppositeIndex] = 0;
                board[last] = 0;
                board[p.getOffset() + Game.BOARD_SIZE] = board[p.getOffset() + Game.BOARD_SIZE] + totalCount;
            }


            String status1 = player1.getLastStatus(board.clone());
            sendMessage(1, "{'lastStatus':" + "'" + status1 + "'}");

            String status2 = player2.getLastStatus(board.clone());
            sendMessage(2, "{'lastStatus':" + "'" + status2 + "'}");

            // check if game ends
            boolean anyFound = false;
            for (int i = 0; i < Game.BOARD_SIZE; i++) {
                if (board[i] != 0) {
                    anyFound = true;
                    break;
                }
            }
            if (anyFound) {
                anyFound = false;
                // check second side
                for (int i = Game.BOARD_SIZE + 1; i < (Game.BOARD_SIZE * 2 + 1); i++) {
                    if (board[i] != 0) {
                        anyFound = true;
                        break;
                    }
                }
            }
            if (!anyFound) {
                finishGameAndSendStatus();
            } else {
                if (p == player1) {
                    // same turn
                    if (last.equals(Game.BOARD_SIZE)) {
                        sendMessage(1, "{'command':'yourturn'}");
                        sendMessage(2, "{'command':'nextturn'}");
                    } else {
                        turn = player2;
                        sendMessage(1, "{'command':'nextturn'}");
                        sendMessage(2, "{'command':'yourturn'}");
                    }
                } else {
                    if (last == Game.BOARD_SIZE * 2 + 1) {
                        sendMessage(1, "{'command':'nextturn'}");
                        sendMessage(2, "{'command':'yourturn'}");
                    } else {
                        turn = player1;
                        sendMessage(1, "{'command':'yourturn'}");
                        sendMessage(2, "{'command':'nextturn'}");
                    }
                }
            }
        }

    }


    private void finishGameAndSendStatus() throws IOException {
        int total = 0;
        for (int i = 0; i < Game.BOARD_SIZE; i++) {
            total = total + board[i];
        }
        board[Game.BOARD_SIZE] = board[Game.BOARD_SIZE] + total;

        total = 0;
        for (int i = Game.BOARD_SIZE + 1; i < (Game.BOARD_SIZE * 2 + 1); i++) {
            total = total + board[i];
        }
        board[Game.BOARD_SIZE * 2 + 1] = board[Game.BOARD_SIZE * 2 + 1] + total;

        if (board[Game.BOARD_SIZE] > board[Game.BOARD_SIZE * 2 + 1]) {
            String m = "{'command':'win:" + board[Game.BOARD_SIZE] + "-" + board[Game.BOARD_SIZE * 2 + 1] + "'}";
            sendMessage(1, m);

            m = "{'command':'lose:" + board[Game.BOARD_SIZE] + "-" + board[Game.BOARD_SIZE * 2 + 1] + "'}";
            sendMessage(2, m);
        } else {
            String m = "{'command':'win:" + board[Game.BOARD_SIZE * 2 + 1] + "-" + board[Game.BOARD_SIZE] + "'}";
            sendMessage(2, m);

            m = "{'command':'lose:" + board[Game.BOARD_SIZE * 2 + 1] + "-" + board[Game.BOARD_SIZE] + "'}";
            sendMessage(1, m);
        }

    }


    private void sendMessage(int i, String msg) throws IOException {
        if (i == 0) {
            // broadcast
            this.player1.getSession().sendMessage(new TextMessage(msg));
            this.player2.getSession().sendMessage(new TextMessage(msg));
        } else if (i == 1) {
            // first player
            this.player1.getSession().sendMessage(new TextMessage(msg));
        } else if (i == 2) {
            // second player
            this.player2.getSession().sendMessage(new TextMessage(msg));
        }
    }


    public boolean isFull() {
        return full;
    }


}
