package proj.pos.bomberman.game;

import org.joml.Vector3f;
import org.joml.Vector4f;
import proj.pos.bomberman.engine.GameItem;
import proj.pos.bomberman.engine.IHud;
import proj.pos.bomberman.engine.graphics.*;
import proj.pos.bomberman.engine.graphics.Window;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Minimap implements IHud {

  private static final Font FONT = new Font("Consolas", Font.PLAIN, 20);

  private static final String CHARSET = "ISO-8859-1";

  private static final float BLOCKSCALE = 20f;
  private static final float AVATARSCALE = 150f;

  private static final float MINIMAPMOVEDX = 10f;
  private static final float MINIMAPMOVEDY = 90f;

  private final Level level;

  private final List<GameItem> gameItems = new ArrayList<>();

  private final TextItem minimapText;
  private final TextItem coordinateText;
  private final GameItem compassItem;
  private final GameItem otherPlayer1;
  private final GameItem otherPlayer2;
  private final GameItem otherPlayer3;
  private final GameItem playerAvatar;
  private final TextItem liveText;

  private final Player player;
  private final Vector3f movedLevel;
  private final float scaleLevel;

  private final Mesh fixBlock;
  private final Mesh destBlock;
  private final Mesh emptyBlock;
  private final Mesh bombBlock;
  private final Mesh powerupSchneller;

  private GameItem[][] blockItems;
  private GameItem[][] specialItems;

  private int lives = 100;
  private int maxlives = 100;

  public Minimap(Level level, Vector3f movedLevel, float scaleLevel, Player player) throws IOException {
    FontTexture fontTexture = new FontTexture(FONT, CHARSET);
    this.level = level;
    this.movedLevel = movedLevel;
    this.scaleLevel = scaleLevel;
    this.player = player;

    // Create compass
    Mesh mesh = OBJLoader.loadMesh("/models/compass.obj");
    Material material = new Material();
    material.setAmbientColor(new Vector4f(1, 0, 0, 1));
    mesh.setMaterial(material);
    compassItem = new GameItem(mesh);
    compassItem.setScale(BLOCKSCALE);
    // Rotate to transform it to screen coordinates
    compassItem.setRotation(0f, 180f, 180f);
    gameItems.add(compassItem);

    // Create other player minimap icons
    mesh = OBJLoader.loadMesh("/models/rectangle.obj");
    Texture texture = new Texture("/textures/playeravatar.png");
    material = new Material(texture, 0f);
    mesh.setMaterial(material);
    otherPlayer1 = new GameItem(mesh);
    otherPlayer1.setScale(BLOCKSCALE);
    otherPlayer1.setRotation(0f, 180f, 180f);
    gameItems.add(otherPlayer1);

    // ----- other player 2
    mesh = OBJLoader.loadMesh("/models/rectangle.obj");
    texture = new Texture("/textures/playeravatar.png");
    material = new Material(texture, 0f);
    mesh.setMaterial(material);
    otherPlayer2 = new GameItem(mesh);
    otherPlayer2.setScale(BLOCKSCALE);
    otherPlayer2.setRotation(0f, 180f, 180f);
    gameItems.add(otherPlayer2);

    // ----- other player 3
    mesh = OBJLoader.loadMesh("/models/rectangle.obj");
    texture = new Texture("/textures/playeravatar.png");
    material = new Material(texture, 0f);
    mesh.setMaterial(material);
    otherPlayer3 = new GameItem(mesh);
    otherPlayer3.setScale(BLOCKSCALE);
    otherPlayer3.setRotation(0f, 180f, 180f);
    gameItems.add(otherPlayer3);


    // Create player avatar
    mesh = OBJLoader.loadMesh("/models/rectangle.obj");
    texture = new Texture("/textures/playeravatar.png");
    material = new Material(texture, 0f);
    mesh.setMaterial(material);
    playerAvatar = new GameItem(mesh);
    playerAvatar.setRotation(0f, 180f, 180f);
    playerAvatar.setScale(AVATARSCALE);
    gameItems.add(playerAvatar);

    //Create TextItems
    this.liveText = new TextItem(lives+" / "+maxlives, fontTexture);
    this.minimapText = new TextItem("Minimap: ", fontTexture);
    this.coordinateText = new TextItem("Coordinates: ", fontTexture);

    this.minimapText.getMesh().getMaterial().setAmbientColor(new Vector4f(1, 1, 1, 1));
    this.coordinateText.getMesh().getMaterial().setAmbientColor(new Vector4f(1, 1, 1, 1));
    this.liveText.getMesh().getMaterial().setAmbientColor(new Vector4f(1,1,1,1));
    this.liveText.setScale(3f);

    gameItems.add(minimapText);
    gameItems.add(coordinateText);
    gameItems.add(liveText);

    fixBlock = OBJLoader.loadMesh("/models/rectangle.obj");
    texture = new Texture("/textures/stone_small.png");
    material = new Material(texture, 0.0f);
    fixBlock.setMaterial(material);

    destBlock = OBJLoader.loadMesh("/models/rectangle.obj");
    texture = new Texture("/textures/brick_small.png");
    material = new Material(texture, 0.0f);
    destBlock.setMaterial(material);

    bombBlock = OBJLoader.loadMesh("/models/rectangle.obj");
    texture = new Texture("/textures/bomb_small.png");
    material = new Material(texture, 0.0f);
    bombBlock.setMaterial(material);

    powerupSchneller = OBJLoader.loadMesh("/models/rectangle.obj");
    texture = new Texture("/textures/powerup_schneller.png");
    material = new Material(texture, 0.0f);
    powerupSchneller.setMaterial(material);

    emptyBlock = OBJLoader.loadMesh("/models/rectangle.obj");
    material = new Material();
    material.setAmbientColor(new Vector4f(1, 1, 1, 1));
    emptyBlock.setMaterial(material);

    doDrawing();
  }

  public void doDrawing() {
    gameItems.clear();
    gameItems.add(minimapText);
    gameItems.add(liveText);
    gameItems.add(coordinateText);
    gameItems.add(playerAvatar);

    // Create blocks
    int[][] layout = level.getLayout();
    blockItems = new GameItem[layout.length][layout[0].length];
    for (int y = 0; y < blockItems.length; y++) {
      for (int x = 0; x < blockItems[y].length; x++) {
        GameItem gameItem;
        if (layout[y][x] == Level.CONSTANT_ID) {
          gameItem = new GameItem(fixBlock);
        } else if (layout[y][x] == Level.DESTROYABLE_ID) {
          gameItem = new GameItem(destBlock);
        } else {
          gameItem = new GameItem(emptyBlock);
        }
        gameItem.setScale(BLOCKSCALE);
        gameItem.setRotation(0f, 180f, 180f);
        blockItems[y][x] = gameItem;
        gameItems.add(gameItem);
      }
    }
    int[][] specialLayout = level.getItemLayout();
    specialItems = new GameItem[specialLayout.length][specialLayout[0].length];
    for (int y = 0; y < specialItems.length; y++) {
      for (int x = 0; x < specialItems[y].length; x++) {
        GameItem gameItem = null;
        switch (specialLayout[y][x]) {
          case Level.BOMB_ID:
            gameItem = new GameItem(bombBlock);
            break;
          case Level.POWERUP_SCHNELLER_ID:
            gameItem = new GameItem(powerupSchneller);
            break;
        }
        if (gameItem != null) {
          gameItem.setScale(BLOCKSCALE);
          gameItem.setRotation(0f, 180f, 180f);
          specialItems[y][x] = gameItem;
          gameItems.add(gameItem);
        }
      }
    }
  }

  @Override
  public GameItem[] getGameItems() {
    return gameItems.toArray(new GameItem[0]);
  }

  public void update(Window window) {
    int[][] layout = level.getLayout();
    this.minimapText.setPosition(10f, 10f, 0);
    this.coordinateText.setPosition(10f, 30f, 0);
    this.liveText.setPosition(20f,window.getHeight() - 80f, 0.999f);
    this.playerAvatar.setPosition(window.getWidth() - AVATARSCALE - 20f, AVATARSCALE + 20f, 0.999f);
    this.liveText.setText(lives+" / "+maxlives);

    this.coordinateText.setText("Coordinates: " + player.getPosition().toString());
    for (int y = 0; y < blockItems.length; y++) {
      for (int x = 0; x < blockItems[y].length; x++) {
        if (blockItems[y][x] != null) {
          float zCoord = 0.988f;
          if (layout[y][x] == Level.EMPTY_ID) {
            zCoord = 0.980f;
          }
          blockItems[y][x].setPosition(MINIMAPMOVEDX + x * BLOCKSCALE, MINIMAPMOVEDY + y * BLOCKSCALE, zCoord);
        }
      }
    }
    for (int y = 0; y < specialItems.length; y++) {
      for (int x = 0; x < specialItems[y].length; x++) {
        if (specialItems[y][x] != null) {
          specialItems[y][x].setPosition(MINIMAPMOVEDX + x * BLOCKSCALE, MINIMAPMOVEDY + y * BLOCKSCALE, 0.987f);
        }
      }
    }
    if (level.insideXZ(player)) {
      if (!gameItems.contains(compassItem)) gameItems.add(compassItem);
      this.compassItem.setPosition(MINIMAPMOVEDX + (player.getPosition().x - movedLevel.x * scaleLevel) * BLOCKSCALE,
              MINIMAPMOVEDY - BLOCKSCALE + (player.getPosition().z - movedLevel.z * scaleLevel) * BLOCKSCALE, 0.989f);

      //später irgendwas mit anderen spielern machen ...
      if(!gameItems.contains(otherPlayer1)) gameItems.add(otherPlayer1);
      this.otherPlayer1.setPosition(MINIMAPMOVEDX * 27 ,MINIMAPMOVEDY + BLOCKSCALE, 0.989f);

      if(!gameItems.contains(otherPlayer2)) gameItems.add(otherPlayer2);
      this.otherPlayer2.setPosition(MINIMAPMOVEDX * 27 ,MINIMAPMOVEDY + BLOCKSCALE * 13, 0.989f);

      if(!gameItems.contains(otherPlayer3)) gameItems.add(otherPlayer3);
      this.otherPlayer3.setPosition(MINIMAPMOVEDX * 3 ,MINIMAPMOVEDY + BLOCKSCALE * 13, 0.989f);

    } else {
      if (gameItems.contains(compassItem)) gameItems.remove(compassItem);
      if(gameItems.contains(otherPlayer1)) gameItems.remove(otherPlayer1);
      if(gameItems.contains(otherPlayer2)) gameItems.remove(otherPlayer2);
      if(gameItems.contains(otherPlayer3)) gameItems.remove(otherPlayer3);
    }
  }

  public void rotateCompass(float angle) {
    this.compassItem.setRotation(0f, 180f, 180f + angle);
  }

  public void rotateOtherPlayer1(float angle){
    this.otherPlayer1.setRotation(0f,180f,180f+angle);
  }

  public void rotateOtherPlayer2(float angle){
    this.otherPlayer2.setRotation(0f,180f,180f+angle);
  }

  public void rotateOtherPlayer3(float angle){
    this.otherPlayer3.setRotation(0f,180f,180f+angle);
  }

  public void setLives(int lives) {
    this.lives = lives;
  }
   public int getLives(){
    return this.lives;
   }
}
