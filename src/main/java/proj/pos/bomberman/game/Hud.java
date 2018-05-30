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

  //private final GameItem compassItem;

  private final GameItem playerAvatar;

  public Hud(String statusText) throws IOException {
    FontTexture fontTexture = new FontTexture(FONT, CHARSET);
    this.statusTextItem = new TextItem(statusText, fontTexture);

    this.statusTextItem.getMesh().getMaterial().setAmbientColor(new Vector4f(1, 1, 1, 1));

    /*// Create compass
    Mesh mesh = OBJLoader.loadMesh("/models/compass.obj");
    Material material = new Material();
    material.setAmbientColor(new Vector4f(1, 0, 0, 1));
    mesh.setMaterial(material);
    compassItem = new GameItem(mesh);
    compassItem.setScale(40.0f);
    // Rotate to transform it to screen coordinates
    compassItem.setRotation(0f, 180f, 180f);*/


    // Create player avatar
    Mesh mesh = OBJLoader.loadMesh("/models/rectangle.obj");
    Texture texture = new Texture("/textures/playeravatar.png");
    Material material = new Material(texture, 0f);
    material.setAmbientColor(new Vector4f(1, 0, 0, 1));
    mesh.setMaterial(material);
    playerAvatar = new GameItem(mesh);
    playerAvatar.setScale(10);
    //compassItem.setScale(40.0f);

    // Create list that holds the items that compose of the HUD
    gameItems = new GameItem[]{statusTextItem/*, compassItem*/, playerAvatar};
  }

  public void setStatusText(String statusText) {
    this.statusTextItem.setText(statusText);
  }

  @Override
  public GameItem[] getGameItems() {
    return gameItems;
  }

  public void update(Window window){
    this.statusTextItem.setPosition(500f, 500f, 0);
    this.playerAvatar.setPosition(500f, 500f, 0);

  }

  public void updateSize(Window window) {
    this.statusTextItem.setPosition(5f, 5f, 0);
    //this.compassItem.setPosition(window.getWidth() - 40f, 50f, 0f);
    this.playerAvatar.setPosition(window.getWidth() - 45f, 45f, 0f);
  }

 /* public void rotateCompass(float angle) {
    this.compassItem.setRotation(0f, 180f, 180 - angle);
  }*/
}
