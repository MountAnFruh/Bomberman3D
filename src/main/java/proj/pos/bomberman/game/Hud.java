package proj.pos.bomberman.game;

import org.joml.Vector4f;
import proj.pos.bomberman.engine.GameItem;
import proj.pos.bomberman.engine.IHud;
import proj.pos.bomberman.engine.graphics.*;
import proj.pos.bomberman.engine.graphics.Window;

import java.awt.*;
import java.io.IOException;

public class Hud implements IHud {

  private static final Font FONT = new Font("Consolas", Font.PLAIN, 20);

  private static final String CHARSET = "ISO-8859-1";

  private final GameItem[] gameItems;

  private final TextItem statusTextItem;

  private final GameItem compassItem;

  public Hud(String statusText) throws IOException {
    FontTexture fontTexture = new FontTexture(FONT, CHARSET);
    this.statusTextItem = new TextItem(statusText, fontTexture);

    this.statusTextItem.getMesh().getMaterial().setAmbientColor(new Vector4f(1, 1, 1, 1));

    // Create compass
    Mesh mesh = OBJLoader.loadMesh("/models/compass.obj");
    Material material = new Material();
    material.setAmbientColor(new Vector4f(1, 0, 0, 1));
    mesh.setMaterial(material);
    compassItem = new GameItem(mesh);
    compassItem.setScale(40.0f);
    // Rotate to transform it to screen coordinates
    compassItem.setRotation(0f, 180f, 180f);

    // Create list that holds the items that compose of the HUD
    gameItems = new GameItem[]{statusTextItem, compassItem};
  }

  public void setStatusText(String statusText) {
    this.statusTextItem.setText(statusText);
  }

  @Override
  public GameItem[] getGameItems() {
    return gameItems;
  }

  public void updateSize(Window window) {
    this.statusTextItem.setPosition(5f, 5f, 0);
    this.compassItem.setPosition(window.getWidth() - 40f, 50f, 0f);
  }

  public void rotateCompass(float angle) {
    this.compassItem.setRotation(0f, 180f, 180 - angle);
  }
}
