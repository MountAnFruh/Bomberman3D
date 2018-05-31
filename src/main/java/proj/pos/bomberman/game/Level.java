package proj.pos.bomberman.game;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.joml.Vector3f;
import proj.pos.bomberman.engine.GameItem;
import proj.pos.bomberman.engine.graphics.BoundingBox;
import proj.pos.bomberman.engine.graphics.Mesh;

import java.util.*;

public class Level {

  public final static int EMPTY_ID = 0;
  public final static int CONSTANT_ID = 1;
  public final static int SPAWN_ID = 2;
  public final static int RANDOM_ID = 3;
  public final static int DESTROYABLE_ID = 4;
  public final static int BOMB_ID = 5;
  public final static int POWERUP_SCHNELLER_ID = 6;

  private static final float bombScale = 0.3f;

  private final Map<Player, List<Bomb>> placedBombs = new HashMap<>();
  private final List<Player> players = new LinkedList<>();

  private Minimap minimap;

  private int[][] layout;
  private int[][] itemLayout;
  private GameItem[][] destroyableItems;
  private GameItem[][] powerupItems;

  private Vector3f moved;
  private float scale;

  private List<GameItem> gameItemsLevel = new ArrayList<>();
  private List<GameItem> powerupLevel = new ArrayList<>();
  private List<Vector3f> spawnPoints = new ArrayList<>();

  private Mesh destroyableBlockMesh = null;
  private Mesh constantBlockMesh = null;
  private Mesh floorBlockMesh = null;
  private Mesh bombMesh = null;
  private Mesh powerupSpeedMesh = null;

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

  public Bomb placeBomb(Player player, int power, float timeToLive) {
    if (bombMesh == null)
      throw new RuntimeException("Bomb Mesh not set!");
    if (insideXZ(player)) {
      float scaleValue = (scale * 2);
      float yCoord = moved.y;
      float xCoord = player.getPosition().x - moved.x;
      float zCoord = player.getPosition().z - moved.z;
      int xLevel = (int) (xCoord / scaleValue);
      int yLevel = (int) (zCoord / scaleValue);
      if(itemLayout[yLevel][xLevel] == EMPTY_ID) {
        itemLayout[yLevel][xLevel] = BOMB_ID;
        if (minimap != null) minimap.doDrawing();
        Bomb bombItem = new Bomb(bombMesh, player, this, power, timeToLive);
        if(!placedBombs.containsKey(player)) placedBombs.put(player, new LinkedList<>());
        placedBombs.get(player).add(bombItem);
        destroyableItems[yLevel][xLevel] = bombItem;
        gameItemsLevel.add(bombItem);
        bombItem.setPosition((xLevel * scaleValue) * scaleValue + 0.5f,
                yCoord * scaleValue + bombScale, (yLevel * scaleValue) * scaleValue + 0.5f);
        bombItem.setScale(bombScale);
        bombItem.setRotation(0, 0, 0);
        return bombItem;
      }
    }
    return null;
  }
//////////////////weitermachn
  public void removePowerup(GameItem pu, Iterator<GameItem> iter)
  {
    float scaleValue = (scale/5);
    int xLevel = (int) (pu.getPosition().x - 0.5f / (scaleValue * 2));
    int yLevel = (int) (pu.getPosition().z - 0.5f / (scaleValue * 2));
    powerupItems[yLevel][xLevel] = null;
    //powerupLevel.remove(pu);
    //gameItemsLevel.remove(pu);
    iter.remove();
    if (minimap != null) minimap.doDrawing();

  }

  public void removeBomb(Player player, Bomb bomb) {
    if(bomb.isExploded()) {
      float scaleValue = (scale * 2);
      int xLevel = (int) (bomb.getPosition().x - 0.5f / (scaleValue * 2));
      int yLevel = (int) (bomb.getPosition().z - 0.5f / (scaleValue * 2));
      itemLayout[yLevel][xLevel] = EMPTY_ID;
      destroyableItems[yLevel][xLevel] = null;
      gameItemsLevel.remove(bomb);
      if(placedBombs.containsKey(player)) {
        placedBombs.get(player).remove(bomb);
      }
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
      explode(xLevel, yLevel);
      for(int x = xLevel + 1;x <= xLevel + power;x++) {
        if(!explode(x, yLevel)) break;
      }
      for(int x = xLevel - 1;x >= xLevel - power;x--) {
        if(!explode(x, yLevel)) break;
      }
      for(int y = yLevel + 1;y <= yLevel + power;y++) {
        if(!explode(xLevel, y)) break;
      }
      for(int y = yLevel - 1;y >= yLevel - power;y--) {
        if(!explode(xLevel, y)) break;
      }
    }
  }

  public boolean explode(int x, int y) {
    float scaleValue = (scale * 2);
    int id = layout[y][x];
    int itemId = itemLayout[y][x];
    float xTileCoordinate = x * scaleValue;
    float zTileCoordinate = y * scaleValue;
    float maxXTileCoordinate = (x+1) * scaleValue;
    float maxZTileCoordinate = (y+1) * scaleValue;
    Vector3f min = new Vector3f(xTileCoordinate, moved.y, zTileCoordinate);
    Vector3f max = new Vector3f(maxXTileCoordinate, moved.y + scaleValue, maxZTileCoordinate);
    BoundingBox bbExplosion = new BoundingBox();
    bbExplosion.setMin(min);
    bbExplosion.setMax(max);
    for(Player player : players) {
      if(player.getBoundingBox().isCollidingWith(bbExplosion)) {
        player.onDeath();
        minimap.setLives(minimap.getLives()-50);
      }
    }
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
    this.powerupItems = new GameItem[layout.length][layout[0].length];
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
            placePowerup(y, x, xCoord, yCoord, zCoord, scaleValue);
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

  private void placePowerup(int y, int x, float xCoord, float yCoord, float zCoord, float scaleValue)
  {
    int prozent = 20;
    int rand = new Random().nextInt(100);
    if(rand < prozent)
    {
      GameItem gameItem = new GameItem(powerupSpeedMesh, "powerup");
      powerupLevel.add(gameItem);
      gameItemsLevel.add(gameItem);
      powerupItems[y][x] = gameItem;

      gameItem.setPosition(xCoord * scaleValue + 0.5f, yCoord * scaleValue + 0.5f, zCoord * scaleValue + 0.5f);
      gameItem.setScale(scale / 5);
      gameItem.setRotation(0, 0, 0);
    }
  }

  public void setMinimap(Minimap minimap) {
    this.minimap = minimap;
  }

  public void setPowerupSpeedMesh(Mesh powerupSpeedMesh) {
    this.powerupSpeedMesh = powerupSpeedMesh;
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

  public Map<Player, List<Bomb>> getPlacedBombs() {
    return placedBombs;
  }

  public List<Player> getPlayers() {
    return players;
  }

  public List<GameItem> getPowerupLevel() {
    return powerupLevel;
  }
}
