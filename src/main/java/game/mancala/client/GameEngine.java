package game.mancala.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class GameEngine {


    private final List<Game> syncGameList = Collections.synchronizedList(new ArrayList<Game>());
    private static GameEngine gameEngine;

    private Game newGame = null;
    private final int player1Iteration[];
    private final int player2Iteration[];

    private GameEngine() {
        int size = Game.BOARD_SIZE * 2 + 2;
        player1Iteration = new int[size - 1];
        // set iterator for player1
        for (int i = 0; i < size - 1; i++) {
            if (i == size - 2) {
                player1Iteration[i] = 0;
            } else {
                player1Iteration[i] = i + 1;
            }
        }
        player2Iteration = new int[size];
        for (int i = 0; i < size; i++) {
            if (i == Game.BOARD_SIZE - 1) {
                player2Iteration[i] = i + 2;
            } else if (i == size - 1) {
                player2Iteration[i] = 0;
            } else {
                player2Iteration[i] = i + 1;
            }
        }

    }

    public static GameEngine instance() {
        if (gameEngine == null) {
            gameEngine = new GameEngine();
        }
        return gameEngine;
    }


    public synchronized Game checkAndCreateNewGame(Player player) throws IOException {
        if (newGame == null || newGame.isFull()) {
            newGame = Game.createNewGame();
            player.setIteration(player1Iteration);
            player.setOffset(0);
            newGame.addPlayer(player);
        } else {
            player.setOffset(Game.BOARD_SIZE + 1);
            player.setIteration(player2Iteration);
            newGame.addPlayer(player);

            if (newGame.isFull()) {
                newGame.startGame();
            }
            syncGameList.add(newGame);
        }
        return newGame;
    }


}
