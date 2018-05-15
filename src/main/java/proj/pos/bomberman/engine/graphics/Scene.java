package proj.pos.bomberman.engine.graphics;

import proj.pos.bomberman.engine.GameItem;
import proj.pos.bomberman.game.SkyBox;

import java.util.ArrayList;
import java.util.List;

public class Scene {

  private List<GameItem> gameItems;

  private SkyBox skyBox;

  private SceneLight sceneLight;

  public Scene() {
    gameItems = new ArrayList<>();
  }

  public List<GameItem> getGameItems() {
    return gameItems;
  }

  public void setGameItems(List<GameItem> gameItems) {
    this.gameItems = gameItems;
  }

  public void cleanupAllGameItems() {
    for (GameItem gameItem : gameItems) {
      gameItem.getMesh().cleanup();
    }
  }

  public SkyBox getSkyBox() {
    return skyBox;
  }

  public void setSkyBox(SkyBox skyBox) {
    this.skyBox = skyBox;
  }

  public SceneLight getSceneLight() {
    return sceneLight;
  }

  public void setSceneLight(SceneLight sceneLight) {
    this.sceneLight = sceneLight;
  }
}
