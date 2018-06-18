package proj.pos.bomberman.engine;

/**
 * @author lwjgldev (angepasst von Andreas Fruhwirt)
 * @since 25.04.2018
 */
public interface IHud {

  GameItem[] getGameItems();

  default void cleanup() {
    GameItem[] gameItems = getGameItems();
    for (GameItem gameItem : gameItems) {
      gameItem.getMesh().cleanup();
    }
  }
}
