package proj.pos.bomberman.game;

import org.joml.Vector3f;
import proj.pos.bomberman.engine.GameItem;
import proj.pos.bomberman.engine.graphics.Mesh;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Level {

  private static final Random rand = new Random();
  private static final float bombScale = 0.3f;

  private Minimap minimap;

  private int[][] layout;

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
      layout[yLevel][xLevel] = 5;
      if (minimap != null) minimap.doDrawing();
      Bomb bombItem = new Bomb(bombMesh, this);
      gameItemsLevel.add(bombItem);
      bombItem.setPosition((xLevel * scaleValue) * scaleValue + 0.5f,
              yCoord * scaleValue + bombScale, (yLevel * scaleValue) * scaleValue + 0.5f);
      bombItem.setScale(bombScale);
      bombItem.setRotation(0, 0, 0);
    }
  }

  public void removeBomb(Bomb bomb) {
    float scaleValue = (scale * 2);
    int xLevel = (int) (bomb.getPosition().x - 0.5f / (scaleValue * 2));
    int yLevel = (int) (bomb.getPosition().z - 0.5f / (scaleValue * 2));
    layout[yLevel][xLevel] = 0;
    gameItemsLevel.remove(bomb);
    if (minimap != null) minimap.doDrawing();
  }

  public void buildMap() {
    if (constantBlockMesh == null || floorBlockMesh == null || destroyableBlockMesh == null)
      throw new RuntimeException("Block Meshes not set!");
    buildFloor();
    for (int y = 0; y < layout.length; y++) {
      for (int x = 0; x < layout[y].length; x++) {
        int id = layout[y][x];
        float xCoord = (x + moved.x);
        float yCoord = moved.y;
        float zCoord = (y + moved.z);
        float scaleValue = (scale * 2);
        if (id == 1 || id == 4) {
          // konstanter Block
          GameItem gameItem;
          if (id == 1) {
            gameItem = new GameItem(constantBlockMesh);
          } else {
            gameItem = new GameItem(destroyableBlockMesh);
          }
          gameItemsLevel.add(gameItem);
          gameItem.setPosition(xCoord * scaleValue + 0.5f, yCoord * scaleValue + 0.5f, zCoord * scaleValue + 0.5f);
          gameItem.setScale(scale);
          gameItem.setRotation(0, 0, 0);
        } else if (id == 2) {
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

  public List<GameItem> getGameItemsLevel() {
    return gameItemsLevel;
  }

  public List<Vector3f> getSpawnPoints() {
    return spawnPoints;
  }
}
