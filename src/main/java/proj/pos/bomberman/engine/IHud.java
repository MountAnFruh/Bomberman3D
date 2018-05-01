package proj.pos.bomberman.engine;

public interface IHud {

  GameItem[] getGameItems();

  default void cleanup() {
    GameItem[] gameItems = getGameItems();
    for (GameItem gameItem : gameItems) {
      gameItem.getMesh().cleanup();
    }
  }
}
