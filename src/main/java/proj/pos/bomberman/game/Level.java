package proj.pos.bomberman.game;

import org.joml.Vector3f;
import proj.pos.bomberman.engine.GameItem;
import proj.pos.bomberman.engine.graphics.Mesh;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Level {

  private static final Random rand = new Random();

  private int[][] layout;

  private List<GameItem> gameItemMap = new ArrayList<>();
  private List<Vector3f> spawnPoints = new ArrayList<>();

  private Mesh destroyableBlockMesh = null;
  private Mesh constantBlockMesh = null;
  private Mesh floorBlockMesh = null;

  public Level(int[][] layout) {
    this.layout = layout;
  }

  private void buildFloor(Vector3f moved, float scale) {
    for (int y = 0; y < layout.length; y++) {
      for (int x = 0; x < layout[y].length; x++) {
        float xCoord = (x + moved.x);
        float yCoord = moved.y;
        float zCoord = (y + moved.z);
        float scaleValue = (scale * 2);
        GameItem gameItem = new GameItem(floorBlockMesh);
        gameItemMap.add(gameItem);
        gameItem.setPosition(xCoord * scaleValue + scale, (yCoord - 1.0f) * scaleValue + scale, zCoord * scaleValue + scale);
        gameItem.setScale(scale);
        gameItem.setRotation(0, 0, 0);
      }
    }
  }

  public boolean insideXZ(Player player, Vector3f moved, float scale) {
    float x = player.getPosition().x - moved.x;
    float z = player.getPosition().z - moved.z;
    float levelMaxX = moved.x + layout[0].length * (scale*2);
    float levelMaxZ = moved.z + layout.length * (scale*2);
    if(x >= 0 && x <= levelMaxX) {
      if(z >= 0 && z <= levelMaxZ) {
        return true;
      }
    }
    return false;
  }

  public void buildMap(Vector3f moved, float scale) {
    if (constantBlockMesh == null || floorBlockMesh == null || destroyableBlockMesh == null)
      throw new RuntimeException("Block Textures not set!");
    buildFloor(moved, scale);
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
          gameItemMap.add(gameItem);
          gameItem.setPosition(xCoord * scaleValue + scale, yCoord * scaleValue + scale, zCoord * scaleValue + scale);
          gameItem.setScale(scale);
          gameItem.setRotation(0, 0, 0);
        } else if (id == 2) {
          // Spawnpoint
          spawnPoints.add(new Vector3f((xCoord) * scaleValue + scale, yCoord * scaleValue + scale, (zCoord) * scaleValue + scale));
        }
      }
    }
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

  public int[][] getLayout() {
    return layout;
  }

  public List<GameItem> getGameItemMap() {
    return gameItemMap;
  }

  public List<Vector3f> getSpawnPoints() {
    return spawnPoints;
  }
}
