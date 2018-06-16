package proj.pos.bomberman.game;

import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;
import proj.pos.bomberman.engine.GameItem;
import proj.pos.bomberman.engine.graphics.Mesh;
import proj.pos.bomberman.engine.graphics.Scene;

import java.util.*;

public class EnemyPlayer extends Player {

  private static Random rand = new Random();

  private int[][] localLayout;
  private int[][] layout;
  private List<Player> enemies;

  public EnemyPlayer(Mesh mesh, Level level, Scene scene) {
    super(mesh, level, scene);
    this.setScale(0.1f);
    this.mesh = mesh;
    this.layout = level.getLayout();
    this.localLayout = new int[layout.length][layout[0].length];
    for (int y = 0; y < layout.length; y++) {
      for (int x = 0; x < layout[y].length; x++) {
        localLayout[y][x] = layout[y][x];
      }
    }
    this.enemies = level.getPlayers();
  }

  @Override
  public void update(double delta) {
    if(this.health > 0) {

//      MainPlayer mainPlayer = null;
//      for (Player player : level.getPlayers()) {
//        if (player instanceof MainPlayer) {
//          mainPlayer = (MainPlayer) player;
//        }
//      }
//      this.getMovementVec().set(-1, 0, 0);
//      float yRotation = 0;
//      if(mainPlayer != null)
//      {
//        yRotation = getYRotationFromPosition(mainPlayer.getPosition());
//        this.setRotation(0, yRotation, 0);
//      }
//
//      float offsetX = movementVec.x * speed;
//      float offsetY = movementVec.y * speed;
//      float offsetZ = movementVec.z * speed;
//
//      this.movePositionFromRotation(offsetX, offsetY, offsetZ);
      if (willBeHitByExplosion(convertToLayout(this.getPosition()))) {
        Vector2i safeSpotLayout = searchNearestSafeCell(convertToLayout(this.getPosition()));
        if (safeSpotLayout != null) {
          Vector3f safeSpot = convertToLevel(safeSpotLayout);
          this.setPosition(safeSpot.x, safeSpot.y, safeSpot.z);
        }
      }

      //this.placeBomb();
    } else {
      this.getMovementVec().set(-1, 0, 0);
      float yRotation = rand.nextFloat() * 360;
      this.setRotation(0, yRotation, 0);

      float offsetX = movementVec.x * speed;
      float offsetY = movementVec.y * speed;
      float offsetZ = movementVec.z * speed;

      this.movePositionFromRotation(offsetX, offsetY, offsetZ);
    }
    super.update(delta);
  }

  private float getYRotationFromPosition(Vector3f position) {
    float xMovement = position.x - this.getPosition().x;
    float yMovement = position.y - this.getPosition().y;
    float zMovement = position.z - this.getPosition().z;

    //this.movePosition(xMovement != 0 ? 1 : 0, yMovement != 0 ? 1 : 0, zMovement != 0 ? -1 : 0);
    float yRotation = (float) Math.toDegrees(Math.atan2(zMovement, xMovement)) + 180;
    return yRotation;
  }

  private Vector2i getNextMove(Vector2i currLoc) {
    Vector2i reachLoc = new Vector2i(currLoc);
    return reachLoc;
  }

  private Vector2i searchNearestSafeCell(Vector2i location) {
    boolean[][] visited = new boolean[layout.length][layout[0].length];
    LinkedList<Vector2i> nodes = new LinkedList<>();
    Vector2i loc_node = new Vector2i(location);
    visited[location.y][location.x] = true;
    nodes.addLast(loc_node);
    while (!nodes.isEmpty()) {
      Vector2i node = nodes.pollFirst();
      visited[node.y][node.x] = true;
      Vector2i[] locations = new Vector2i[]{
              new Vector2i(node.x + 1, node.y),
              new Vector2i(node.x - 1, node.y),
              new Vector2i(node.x, node.y - 1),
              new Vector2i(node.x, node.y + 1)
      };
      for (Vector2i loc : locations) {
        if (visited[loc.y][loc.x] == true) continue;
        int loc_id = layout[loc.y][loc.x];
        if (loc_id == Level.EMPTY_ID || loc_id == Level.SPAWN_ID) {
          if (!willBeHitByExplosion(loc)) {
            return loc;
          } else {
            Vector2i l_node = new Vector2i(loc);
            nodes.addLast(l_node);
          }
        }
      }
    }
    return null;
  }

  private boolean willBeHitByExplosion(Vector2i location) {
    Map<Player, List<Bomb>> placedBombs = level.getPlacedBombs();
    Explosion[][] explosions = level.getExplosionItems();
    List<Explosion> explosionList = new ArrayList<>();
    for (int i = 0; i < explosions.length; i++) {
      for (int j = 0; j < explosions[i].length; j++) {
        if (explosions[i][j] != null) explosionList.add(explosions[i][j]);
      }
    }
    for (Explosion explosion : explosionList) {
      Vector2i loc = convertToLayout(explosion.getPosition());
      if (location.x == loc.x && location.y == loc.y) return true;
    }
    for (Player player : placedBombs.keySet()) {
      for (Bomb bomb : placedBombs.get(player)) {
        int power = bomb.getPower();
        Vector2i location_bomb = convertToLayout(bomb.getPosition());
        for (int x = location_bomb.x; x <= location_bomb.x + power; x++) {
          if (layout[location_bomb.y][x] == Level.DESTROYABLE_ID) break;
          if (location.x == x && location.y == location_bomb.y) return true;
        }
        for (int x = location_bomb.x; x >= location_bomb.x - power; x--) {
          if (layout[location_bomb.y][x] == Level.DESTROYABLE_ID) break;
          if (location.x == x && location.y == location_bomb.y) return true;
        }
        for (int y = location_bomb.y; y <= location_bomb.y + power; y++) {
          if (layout[y][location_bomb.x] == Level.DESTROYABLE_ID) break;
          if (location.x == location_bomb.x && location.y == y) return true;
        }
        for (int y = location_bomb.y; y >= location_bomb.y - power; y--) {
          if (layout[y][location_bomb.x] == Level.DESTROYABLE_ID) break;
          if (location.x == location_bomb.x && location.y == y) return true;
        }
      }
    }
    return false;
  }

  @Override
  public void onDeath() {
    super.onDeath();
    this.getMesh().getMaterial().setAmbientColor(new Vector4f(0,0,0,1));
  }

  @Override
  public void doCollisions(Vector3f oldPos, Vector3f currentPos, List<GameItem> noCollision) {
    super.doCollisions(oldPos, currentPos, noCollision);
  }

  private Vector2i convertToLayout(Vector3f location) {
    float scaleValue = (level.getScale() * 2);
    float xCoord = location.x - level.getMoved().x;
    float zCoord = location.z - level.getMoved().z;
    int xLevel = (int) (xCoord / scaleValue);
    int yLevel = (int) (zCoord / scaleValue);
    return new Vector2i(xLevel, yLevel);
  }

  private Vector3f convertToLevel(Vector2i layoutLoc) {
    float xCoord = (layoutLoc.x + level.getMoved().x);
    float yCoord = level.getMoved().y;
    float zCoord = (layoutLoc.y + level.getMoved().z);
    float scaleValue = (level.getScale() * 2);
    Vector3f loc = new Vector3f(xCoord * scaleValue + scaleValue / 2, yCoord * scaleValue + scaleValue / 2, zCoord * scaleValue + scaleValue / 2);
    return loc;
  }

}
