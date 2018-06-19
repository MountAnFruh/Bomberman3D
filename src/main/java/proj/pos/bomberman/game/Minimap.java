package proj.pos.bomberman.game;

import org.joml.Vector3f;
import org.joml.Vector4f;
import proj.pos.bomberman.engine.GameItem;
import proj.pos.bomberman.engine.IHud;
import proj.pos.bomberman.engine.graphics.*;
import proj.pos.bomberman.engine.graphics.Window;
import proj.pos.bomberman.engine.sound.SoundManager;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nico Prosser, Robert Schmölzer
 * @since 07.05.2018
 */
public class Minimap implements IHud {

  private static final Font FONT = new Font("Consolas", Font.PLAIN, 20);

  private static final String CHARSET = "ISO-8859-1";

  private static final float BLOCKSCALE = 20f;
  private static final float AVATARSCALE = 150f;

  private static final float MINIMAPMOVEDX = 10f;
  private static final float MINIMAPMOVEDY = 90f;

  private float windowX = 1920f;
  private float windowY = 1080f;
  private final Level level;

  private final List<GameItem> gameItems = new ArrayList<>();

  private final TextItem[] powerupTextItems;
  //private final TextItem minimapText;
  private final TextItem middleTextItem;
  //private final TextItem coordinateText;
  private final GameItem compassItem;
  private final List<GameItem> compassItemEnemies = new ArrayList<>();
  private final GameItem playerAvatar;
  private final GameItem[] powerupItemImg;
  private final TextItem liveText;

  private final MainPlayer mainPlayer;
  private final List<EnemyPlayer> enemyPlayers;
  private final Vector3f movedLevel;
  private final float scaleLevel;

  private final Mesh fixBlock;
  private final Mesh destBlock;
  private final Mesh emptyBlock;
  private final Mesh floorBlock;
  private final Mesh bombBlock;
  private final Mesh powerupSchneller;
  private final Mesh powerupMehrBombs;
  private final Mesh powerupMehrReich;
  private final Mesh explosionBlock;

  private GameItem[][] blockItems;
  private GameItem[][] specialItems;
  private GameItem[][] explosionItems;
  private int[] powerupAnz;

  private SoundManager soundManager;

  public Minimap(Level level, Vector3f movedLevel, float scaleLevel, MainPlayer mainPlayer, List<EnemyPlayer> enemyPlayers) throws IOException {
    FontTexture fontTexture = new FontTexture(FONT, CHARSET);
    Texture texture;
    this.level = level;
    this.movedLevel = movedLevel;
    this.scaleLevel = scaleLevel;
    this.mainPlayer = mainPlayer;
    this.enemyPlayers = enemyPlayers;

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

    for (Player player : enemyPlayers) {
      mesh = OBJLoader.loadMesh("/models/rectangle_centered.obj");
      texture = new Texture("/textures/playeravatar.png");
      material = new Material(texture, 0f);
      mesh.setMaterial(material);
      GameItem otherPlayer = new GameItem(mesh);
      otherPlayer.setScale(BLOCKSCALE);
      otherPlayer.setRotation(0f, 180f, 180f);
      gameItems.add(otherPlayer);
      compassItemEnemies.add(otherPlayer);
    }

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
    this.liveText = new TextItem(mainPlayer.getHealth() + " / " + mainPlayer.getMaxHealth(), fontTexture);
    //this.minimapText = new TextItem("Minimap: ", fontTexture);
    //this.coordinateText = new TextItem("Coordinates: ", fontTexture);
    this.middleTextItem = new TextItem("You are Dead", fontTexture);

    powerupTextItems = new TextItem[3];
    this.powerupTextItems[0] = new TextItem("x0 ", fontTexture);
    this.powerupTextItems[1] = new TextItem("x0 ", fontTexture);
    this.powerupTextItems[2] = new TextItem("x0 ", fontTexture);
    powerupTextItems[0].setScale(2);
    powerupTextItems[1].setScale(2);
    powerupTextItems[2].setScale(2);

    powerupItemImg = new GameItem[3];
    mesh = OBJLoader.loadMesh("/models/rectangle.obj");
    texture = new Texture("/textures/powerup_schneller_icon.png");
    material = new Material(texture, 0f);
    mesh.setMaterial(material);
    powerupItemImg[0] = new GameItem(mesh);
    powerupItemImg[0].setRotation(0f, 180f, 180f);
    powerupItemImg[0].setScale(BLOCKSCALE*4);
    gameItems.add(powerupItemImg[0]);

    mesh = OBJLoader.loadMesh("/models/rectangle.obj");
    texture = new Texture("/textures/powerup_mehr_bomben_icon.png");
    material = new Material(texture, 0f);
    mesh.setMaterial(material);
    powerupItemImg[1] = new GameItem(mesh);
    powerupItemImg[1].setRotation(0f, 180f, 180f);
    powerupItemImg[1].setScale(BLOCKSCALE*4);
    gameItems.add(powerupItemImg[0]);

    mesh = OBJLoader.loadMesh("/models/rectangle.obj");
    texture = new Texture("/textures/powerup_mehr_reichweite_icon.png");
    material = new Material(texture, 0f);
    mesh.setMaterial(material);
    powerupItemImg[2] = new GameItem(mesh);
    powerupItemImg[2].setRotation(0f, 180f, 180f);
    powerupItemImg[2].setScale(BLOCKSCALE*4);
    gameItems.add(powerupItemImg[0]);

    powerupAnz = new int[3];
    powerupAnz[0] = 0;
    powerupAnz[1] = 0;
    powerupAnz[2] = 0;

    middleTextItem.setScale(10f);

    //this.minimapText.getMesh().getMaterial().setAmbientColor(new Vector4f(1, 1, 1, 1));
    this.powerupTextItems[0].getMesh().getMaterial().setAmbientColor(new Vector4f(0, 0, 0, 1));
    this.powerupTextItems[1].getMesh().getMaterial().setAmbientColor(new Vector4f(0, 0, 0, 1));
    this.powerupTextItems[2].getMesh().getMaterial().setAmbientColor(new Vector4f(0, 0, 0, 1));
    this.powerupItemImg[0].getMesh().getMaterial().setAmbientColor(new Vector4f(1, 1, 1, 1));
    this.powerupItemImg[1].getMesh().getMaterial().setAmbientColor(new Vector4f(1, 1, 1, 1));
    this.powerupItemImg[2].getMesh().getMaterial().setAmbientColor(new Vector4f(1, 1, 1, 1));
    //this.coordinateText.getMesh().getMaterial().setAmbientColor(new Vector4f(1, 1, 1, 1));
    this.liveText.getMesh().getMaterial().setAmbientColor(new Vector4f(1, 1, 1, 1));
    this.liveText.setScale(3f);

    //gameItems.add(minimapText);
    //gameItems.add(coordinateText);
    gameItems.add(liveText);
    gameItems.add(powerupTextItems[0]);
    gameItems.add(powerupTextItems[1]);
    gameItems.add(powerupTextItems[2]);
    gameItems.add(powerupItemImg[0]);
    gameItems.add(powerupItemImg[1]);
    gameItems.add(powerupItemImg[2]);

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
    texture = new Texture("/textures/powerup_schneller_icon.png");
    material = new Material(texture, 0.0f);
    powerupSchneller.setMaterial(material);

    powerupMehrBombs = OBJLoader.loadMesh("/models/rectangle.obj");
    texture = new Texture("/textures/powerup_mehr_bomben_icon.png");
    material = new Material(texture, 0.0f);
    powerupMehrBombs.setMaterial(material);

    powerupMehrReich = OBJLoader.loadMesh("/models/rectangle.obj");
    texture = new Texture("/textures/powerup_mehr_reichweite_icon.png");
    material = new Material(texture, 0.0f);
    powerupMehrReich.setMaterial(material);

    emptyBlock = OBJLoader.loadMesh("/models/rectangle.obj");
    material = new Material();
    material.setAmbientColor(new Vector4f(1, 1, 1, 1));
    emptyBlock.setMaterial(material);

    floorBlock = OBJLoader.loadMesh("/models/rectangle.obj");
    //texture = new Texture("/textures/stone_dark_small.png");
    texture = new Texture("/textures/wiese_small.png");
    material = new Material(texture, 0.0f);
    floorBlock.setMaterial(material);

    explosionBlock = OBJLoader.loadMesh("/models/rectangle.obj");
    texture = new Texture("/textures/explosion_oben.png");
    material = new Material(texture, 0.0f);
    explosionBlock.setMaterial(material);

    doDrawing();
  }

  public void doDrawing() {
    gameItems.clear();
    //gameItems.add(minimapText);
    gameItems.add(liveText);
    //gameItems.add(coordinateText);
    gameItems.add(playerAvatar);
    gameItems.add(middleTextItem);
    gameItems.add(powerupTextItems[0]);
    gameItems.add(powerupTextItems[1]);
    gameItems.add(powerupTextItems[2]);
    gameItems.add(powerupItemImg[0]);
    gameItems.add(powerupItemImg[1]);
    gameItems.add(powerupItemImg[2]);

    if(mainPlayer.isDead()) {
      middleTextItem.setText("You are Dead");
      gameItems.add(middleTextItem);
      level.setMainPlayerDead(true);
    } else if(level.getPlayers().size() == 1 && level.getPlayers().get(0) == mainPlayer) {
      middleTextItem.setText("You won");
      soundManager.playSoundSource(BombermanGame.Sounds.WIN.name());
      gameItems.add(middleTextItem);
    } else {
      gameItems.remove(middleTextItem);
    }

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
          //gameItem = new GameItem(emptyBlock);
          gameItem = new GameItem(floorBlock);
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
            gameItem.setScale(BLOCKSCALE);
            break;
          case Level.POWERUP_SCHNELLER_ID:
            gameItem = new GameItem(powerupSchneller);
            gameItem.setScale(BLOCKSCALE / 2);
            break;
          case Level.POWERUP_MEHR_BOMBEN_ID:
            gameItem = new GameItem(powerupMehrBombs);
            gameItem.setScale(BLOCKSCALE / 2);
            break;
          case Level.POWERUP_MEHR_REICHWEITE_ID:
            gameItem = new GameItem(powerupMehrReich);
            gameItem.setScale(BLOCKSCALE / 2);
            break;
        }
        if (gameItem != null) {
          gameItem.setRotation(0f, 180f, 180f);
          specialItems[y][x] = gameItem;
          gameItems.add(gameItem);
        }
      }
    }
    int[][] explosionLayout = level.getExplosionLayout();
    explosionItems = new GameItem[explosionLayout.length][explosionLayout[0].length];
    for(int y = 0;y < explosionLayout.length;y++) {
      for(int x = 0;x < explosionLayout[y].length;x++) {
        if(explosionLayout[y][x] == Level.EXPLOSION_ID) {
          GameItem gameItem = new GameItem(explosionBlock);
          gameItem.setScale(BLOCKSCALE);
          gameItem.setRotation(0f, 180f, 180f);
          explosionItems[y][x] = gameItem;
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
    windowX = window.getWidth();
    windowY = window.getHeight();
    int[][] layout = level.getLayout();
    float posX = windowX-210;
    float posY = windowY-300;
    //this.minimapText.setPosition(10f, 10f, 0);
    this.powerupTextItems[0].setPosition(posX, posY, 0);
    this.powerupTextItems[1].setPosition(posX, posY+100, 0);
    this.powerupTextItems[2].setPosition(posX, posY+200, 0);
    this.powerupItemImg[0].setPosition(posX+90, posY+60, 0);
    this.powerupItemImg[1].setPosition(posX+90, posY+60+100, 0);
    this.powerupItemImg[2].setPosition(posX+90, posY+60+200, 0);
    this.middleTextItem.setPosition(window.getWidth() / 2 - this.middleTextItem.getBoundingBox().getSize().x / 2
            , window.getHeight() / 2 - this.middleTextItem.getBoundingBox().getSize().y / 2, 0);
   // this.coordinateText.setPosition(10f, 30f, 0);
    this.liveText.setPosition(20f, window.getHeight() - 80f, 0.999f);
    this.playerAvatar.setPosition(window.getWidth() - AVATARSCALE - 20f, AVATARSCALE + 20f, 0.999f);
    this.liveText.setText(mainPlayer.getHealth() + " / " + mainPlayer.getMaxHealth());

   // this.coordinateText.setText("Coordinates: " + mainPlayer.getPosition().toString());
    for (int y = 0; y < blockItems.length; y++) {
      for (int x = 0; x < blockItems[y].length; x++) {
        if (blockItems[y][x] != null) {
          float zCoord = 0.988f;
          if (layout[y][x] == Level.EMPTY_ID || layout[y][x] == Level.SPAWN_ID) {
            zCoord = 0.980f;
          }
          blockItems[y][x].setPosition(MINIMAPMOVEDX + x * BLOCKSCALE, MINIMAPMOVEDY + y * BLOCKSCALE, zCoord);
        }
      }
    }
    for (int y = 0; y < specialItems.length; y++) {
      for (int x = 0; x < specialItems[y].length; x++) {
        if (specialItems[y][x] != null) {
          int[][] specialLayout = level.getItemLayout();
          if (specialLayout[y][x] == Level.POWERUP_SCHNELLER_ID || specialLayout[y][x] == Level.POWERUP_MEHR_BOMBEN_ID || specialLayout[y][x] == Level.POWERUP_MEHR_REICHWEITE_ID) {
            specialItems[y][x].setPosition(MINIMAPMOVEDX + x * BLOCKSCALE + 5f, MINIMAPMOVEDY + y * BLOCKSCALE - 5f, 0.987f);
          }else{
            specialItems[y][x].setPosition(MINIMAPMOVEDX + x * BLOCKSCALE, MINIMAPMOVEDY + y * BLOCKSCALE, 0.989f);
          }

        }
      }
    }
    for(int y = 0;y < explosionItems.length;y++) {
      for(int x = 0;x < explosionItems[y].length;x++) {
        if(explosionItems[y][x] != null) {
          explosionItems[y][x].setPosition(MINIMAPMOVEDX + x * BLOCKSCALE, MINIMAPMOVEDY + y * BLOCKSCALE, 0.988f);
        }
      }
    }
    if (level.insideXZ(mainPlayer)) {
      if (!gameItems.contains(compassItem)) gameItems.add(compassItem);
      this.compassItem.setPosition(MINIMAPMOVEDX + (mainPlayer.getPosition().x - movedLevel.x * scaleLevel) * BLOCKSCALE,
              MINIMAPMOVEDY - BLOCKSCALE + (mainPlayer.getPosition().z - movedLevel.z * scaleLevel) * BLOCKSCALE, 0.990f);

      for (int i = 0; i < enemyPlayers.size(); i++) {
        GameItem compassItem = compassItemEnemies.get(i);
        Player enemy = enemyPlayers.get(i);
        if (!gameItems.contains(compassItem) && !enemy.isDead()) gameItems.add(compassItem);
        if (enemy.isDead()) gameItems.remove(compassItem);
        compassItem.setPosition(MINIMAPMOVEDX + (enemy.getPosition().x - movedLevel.x * scaleLevel) * BLOCKSCALE,
                MINIMAPMOVEDY - BLOCKSCALE + (enemy.getPosition().z - movedLevel.z * scaleLevel) * BLOCKSCALE, 0.990f);
      }
    } else {
      if (gameItems.contains(compassItem)) gameItems.remove(compassItem);
    }
  }

  public void rotateCompass(float angle) {
    this.compassItem.setRotation(0f, 180f, 180f + angle);
  }

  public void powerupPickedUp(int id)
  {
    powerupTextItems[id-5].setText(powerupTextItems[id-5].getText().replaceAll(powerupAnz[id-5]+"", ++powerupAnz[id-5]+""));

    this.powerupTextItems[0].getMesh().getMaterial().setAmbientColor(new Vector4f(0, 0, 0, 1));
    this.powerupTextItems[1].getMesh().getMaterial().setAmbientColor(new Vector4f(0, 0, 0, 1));
    this.powerupTextItems[2].getMesh().getMaterial().setAmbientColor(new Vector4f(0, 0, 0, 1));
  }

  public void setSoundManager(SoundManager soundManager) {
    this.soundManager = soundManager;
  }

}
