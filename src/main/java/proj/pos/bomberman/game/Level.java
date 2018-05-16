package proj.pos.bomberman.game;

import org.joml.Vector3f;
import proj.pos.bomberman.engine.GameItem;
import proj.pos.bomberman.engine.graphics.Mesh;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Level {

  public static int EMPTY_ID = 0;
  public static int CONSTANT_ID = 1;
  public static int SPAWN_ID = 2;
  public static int RANDOM_ID = 3;
  public static int DESTROYABLE_ID = 4;
  public static int BOMB_ID = 5;

  private static final Random rand = new Random();
  private static final float bombScale = 0.3f;

  private Minimap minimap;

  private int[][] layout;
  private int[][] itemLayout;
  private GameItem[][] destroyableItems;

  private Vector3f moved;
  private float scale;

  private List<GameItem> gameItemsLevel = new ArrayList<>();
  private List<Vector3f> spawnPoints = new ArrayList<>();

  private Mesh destroyableBlockMesh = null;
  private Mesh constantBlockMesh = null;
  private Mesh floorBlockMesh = null;
  private Mesh bombMesh = null;

  public Level(int[][] layout, Vector3f moved, float scale) {
    this.moved = moved;
    this.scale = scale;
    this.layout = layout;
    this.itemLayout = new int[layout.length][layout[0].length];
  }

  private void buildFloor() {
    for (int y = 0; y < layout.length; y++) {
      for (int x = 0; x < layout[y].length; x++) {
        float xCoord = (x + moved.x);
        float yCoord = moved.y;
        float zCoord = (y + moved.z);
        float scaleValue = (scale * 2);
        GameItem gameItem = new GameItem(floorBlockMesh);
        gameItemsLevel.add(gameItem);
        gameItem.setPosition(xCoord * scaleValue + scale, (yCoord - 1.0f) * scaleValue + scale, zCoord * scaleValue + scale);
        gameItem.setScale(scale);
        gameItem.setRotation(0, 0, 0);
      }
    }
  }

  public boolean insideXZ(Player player) {
    float x = player.getPosition().x - moved.x;
    float z = player.getPosition().z - moved.z;
    float levelMaxX = moved.x + layout[0].length * (scale * 2);
    float levelMaxZ = moved.z + layout.length * (scale * 2);
    if (x >= 0 && x <= levelMaxX) {
      if (z >= 0 && z <= levelMaxZ) {
        return true;
      }
    }
    return false;
  }

  public void placeBomb(Player player) {
    if (bombMesh == null)
      throw new RuntimeException("Bomb Mesh not set!");
    if (insideXZ(player)) {
      float scaleValue = (scale * 2);
      float yCoord = moved.y;
      float xCoord = player.getPosition().x - moved.x;
      float zCoord = player.getPosition().z - moved.z;
      int xLevel = (int) (xCoord / scaleValue);
      int yLevel = (int) (zCoord / scaleValue);
      itemLayout[yLevel][xLevel] = BOMB_ID;
      if (minimap != null) minimap.doDrawing();
      Bomb bombItem = new Bomb(bombMesh, this, 3);
      destroyableItems[yLevel][xLevel] = bombItem;
      gameItemsLevel.add(bombItem);
      bombItem.setPosition((xLevel * scaleValue) * scaleValue + 0.5f,
              yCoord * scaleValue + bombScale, (yLevel * scaleValue) * scaleValue + 0.5f);
      bombItem.setScale(bombScale);
      bombItem.setRotation(0, 0, 0);
    }
  }

  public void removeBomb(Bomb bomb) {
    if(bomb.isExploded()) {
      float scaleValue = (scale * 2);
      int xLevel = (int) (bomb.getPosition().x - 0.5f / (scaleValue * 2));
      int yLevel = (int) (bomb.getPosition().z - 0.5f / (scaleValue * 2));
      itemLayout[yLevel][xLevel] = EMPTY_ID;
      destroyableItems[yLevel][xLevel] = null;
      gameItemsLevel.remove(bomb);
      if (minimap != null) minimap.doDrawing();
    }
  }

  public void explodeBomb(Bomb bomb) {
    bomb.setExploded(true);
    float scaleValue = (scale * 2);
    int xLevel = (int) (bomb.getPosition().x - 0.5f / (scaleValue * 2));
    int yLevel = (int) (bomb.getPosition().z - 0.5f / (scaleValue * 2));
    if (itemLayout[yLevel][xLevel] == BOMB_ID) {
      int power = bomb.getPower();
      for(int x = xLevel + 1;x <= xLevel + power;x++) {
        if(!destroy(x, yLevel)) break;
      }
      for(int x = xLevel - 1;x >= xLevel - power;x--) {
        if(!destroy(x, yLevel)) break;
      }
      for(int y = yLevel + 1;y <= yLevel + power;y++) {
        if(!destroy(xLevel, y)) break;
      }
      for(int y = yLevel - 1;y >= yLevel - power;y--) {
        if(!destroy(xLevel, y)) break;
      }
    }
  }

  public boolean destroy(int x, int y) {
    int id = layout[y][x];
    int itemId = itemLayout[y][x];
    if(id == CONSTANT_ID) {
      return false;
    }
    if(itemId == BOMB_ID) {
      Bomb bomb2 = (Bomb) destroyableItems[y][x];
      if(!bomb2.isExploded()) {
        this.explodeBomb(bomb2);
      }
      return false;
    }
    if(id == DESTROYABLE_ID) {
      layout[y][x] = EMPTY_ID;
      gameItemsLevel.remove(destroyableItems[y][x]);
      destroyableItems[y][x] = null;
      return false;
    }
    return true;
  }

  public void buildMap() {
    if (constantBlockMesh == null || floorBlockMesh == null || destroyableBlockMesh == null)
      throw new RuntimeException("Block Meshes not set!");
    buildFloor();
    this.destroyableItems = new GameItem[layout.length][layout[0].length];
    for (int y = 0; y < layout.length; y++) {
      for (int x = 0; x < layout[y].length; x++) {
        int id = layout[y][x];
        float xCoord = (x + moved.x);
        float yCoord = moved.y;
        float zCoord = (y + moved.z);
        float scaleValue = (scale * 2);
        if (id == CONSTANT_ID || id == DESTROYABLE_ID) {
          // konstanter Block
          GameItem gameItem;
          if (id == CONSTANT_ID) {
            gameItem = new GameItem(constantBlockMesh);
          } else {
            gameItem = new GameItem(destroyableBlockMesh);
            destroyableItems[y][x] = gameItem;
          }
          gameItemsLevel.add(gameItem);
          gameItem.setPosition(xCoord * scaleValue + 0.5f, yCoord * scaleValue + 0.5f, zCoord * scaleValue + 0.5f);
          gameItem.setScale(scale);
          gameItem.setRotation(0, 0, 0);
        } else if (id == SPAWN_ID) {
          // Spawnpoint
          spawnPoints.add(new Vector3f((xCoord) * scaleValue + 0.5f, yCoord * scaleValue + 0.5f, (zCoord) * scaleValue + 0.5f));
        }
      }
    }
  }

  public void setMinimap(Minimap minimap) {
    this.minimap = minimap;
  }

  public void setConstantBlockMesh(Mesh constantBlockMesh) {
    this.constantBlockMesh = constantBlockMesh;
  }

  public void setDestroyableBlockMesh(Mesh destroyableBlockMesh) {
    this.destroyableBlockMesh = destroyableBlockMesh;
  }

  public void setFloorBlockMesh(Mesh floorBlockMesh) {
    this.floorBlockMesh = floorBlockMesh;
  }

  public void setBombMesh(Mesh bombMesh) {
    this.bombMesh = bombMesh;
  }

  public int[][] getLayout() {
    return layout;
  }

  public int[][] getItemLayout() {
    return itemLayout;
  }

  public List<GameItem> getGameItemsLevel() {
    return gameItemsLevel;
  }

  public List<Vector3f> getSpawnPoints() {
    return spawnPoints;
  }
}
