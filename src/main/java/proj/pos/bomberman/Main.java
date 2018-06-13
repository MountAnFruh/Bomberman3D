package proj.pos.bomberman;

import proj.pos.bomberman.engine.GameEngine;
import proj.pos.bomberman.engine.IGameLogic;
import proj.pos.bomberman.engine.graphics.Window;
import proj.pos.bomberman.game.BombermanGame;

public class Main {

  public static void main(String[] args) {
    IGameLogic gameLogic = new BombermanGame();

    GameEngine gameEngine = new GameEngine("Bomberman 3D",
            600, 600, gameLogic);
    gameEngine.start();
  }
}
