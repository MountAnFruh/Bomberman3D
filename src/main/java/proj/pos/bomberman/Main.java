package proj.pos.bomberman;

import proj.pos.bomberman.engine.GameEngine;
import proj.pos.bomberman.engine.IGameLogic;

public class Main {

  public static void main(String[] args) {
    System.setProperty("java.awt.headless","true");
    IGameLogic gameLogic = new DummyGame();
    GameEngine gameEngine = new GameEngine("Bomberman 3D",
            300, 300, gameLogic);
    gameEngine.start();
  }
}
