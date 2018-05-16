package proj.pos.bomberman;

import proj.pos.bomberman.engine.GameEngine;
import proj.pos.bomberman.engine.IGameLogic;
import proj.pos.bomberman.game.DummyGame;

public class Main {

  public static void main(String[] args) {
    IGameLogic gameLogic = new DummyGame();
    GameEngine gameEngine = new GameEngine("Bomberman 3D",
            1200, 1200, gameLogic);
    gameEngine.start();
  }
}
