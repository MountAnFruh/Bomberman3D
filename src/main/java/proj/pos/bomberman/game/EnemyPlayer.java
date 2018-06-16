package proj.pos.bomberman.game;

import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;
import proj.pos.bomberman.engine.GameItem;
import proj.pos.bomberman.engine.graphics.BoundingBox;
import proj.pos.bomberman.engine.graphics.Mesh;
import proj.pos.bomberman.engine.graphics.Scene;

import java.util.*;

public class EnemyPlayer extends Player {

  private static Random rand = new Random();

  private int[][] layout;
  private int[][] expl_Layout;
  private int[][] item_layout;
  private List<Player> enemies;

  public EnemyPlayer(Mesh mesh, Level level, Scene scene) {
    super(mesh, level, scene);
    this.setScale(0.1f);
    this.mesh = mesh;
    this.layout = level.getLayout();
    this.expl_Layout = level.getExplosionLayout();
    this.item_layout = level.getItemLayout();
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
      this.placeBomb();
      if (willBeHitByExplosion(this.getPosition())) {
        Vector3f safeSpot = searchNearestSafeSpot(this.getPosition());
        if (safeSpot != null) {
          safeSpot = convertToLevel(convertToLayout(safeSpot));
          this.getMovementVec().set(-1, 0, 0);
          this.setRotation(0, getYRotationFromPosition(safeSpot), 0);

          float offsetX = movementVec.x * speed;
          float offsetY = movementVec.y * speed;
          float offsetZ = movementVec.z * speed;
          this.movePositionFromRotation(offsetX, offsetY, offsetZ);
        }
      }
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

  private Vector3f searchNearestSafeSpot(Vector3f location) {
    List<Vector3f> visitedLocs = new ArrayList<>();
    LinkedList<Vector3f> nodes = new LinkedList<>();
    Vector3f loc_node = new Vector3f(location);
    visitedLocs.add(loc_node);
    nodes.addLast(loc_node);
    while (!nodes.isEmpty()) {
      Vector3f node = nodes.pollFirst();
      Vector2i node_layout = convertToLayout(node);
      if (node_layout.x < 0 || node_layout.y < 0 ||
              node_layout.y >= layout.length || node_layout.x >= layout[0].length) continue;
      int node_id = layout[node_layout.y][node_layout.x];
      int node_item_id = item_layout[node_layout.y][node_layout.x];
      int node_expl_id = expl_Layout[node_layout.y][node_layout.x];
      if ((node_id == Level.EMPTY_ID || node_id == Level.SPAWN_ID) &&
              node_item_id != Level.BOMB_ID && node_expl_id != Level.EXPLOSION_ID) {
        if (!willBeHitByExplosion(node)) {
          return node;
        }
      }
      Vector3f[] locations = new Vector3f[]{
              new Vector3f(node.x + level.getScale() / 2, node.y, node.z),
              new Vector3f(node.x - level.getScale() / 2, node.y, node.z),
              new Vector3f(node.x, node.y, node.z - level.getScale() / 2),
              new Vector3f(node.x, node.y, node.z + level.getScale() / 2)
      };
      for (Vector3f loc : locations) {
        if (visitedLocs.stream().anyMatch(l -> {
          return l.x == loc.x && l.y == loc.y && l.z == loc.z;
        })) continue;
        visitedLocs.add(loc);
        Vector2i loc_layout = convertToLayout(loc);
        if (loc_layout.x < 0 || loc_layout.y < 0 ||
                loc_layout.y >= layout.length || loc_layout.x >= layout[0].length) continue;
        int loc_id = layout[loc_layout.y][loc_layout.x];
        int loc_item_id = item_layout[loc_layout.y][loc_layout.x];
        if ((loc_id == Level.EMPTY_ID || loc_id == Level.SPAWN_ID) && !isInsideBomb(loc)) {
          if (!isHitByExplosion(loc)) {
            nodes.addLast(loc);
          }
        }
      }
    }
    return null;
  }

  private boolean isInsideBomb(Vector3f location) {
    for (Player player : level.getPlacedBombs().keySet()) {
      for (Bomb bomb : level.getPlacedBombs().get(player)) {
        Vector2i loc_location = convertToLayout(location);
        Vector2i loc_bomb = convertToLayout(bomb.getPosition());
        if (loc_location.x == loc_bomb.x && loc_location.y == loc_bomb.y) {
          if (noCollision.contains(bomb)) continue;
          return true;
        }
      }
    }
    return false;
  }

  private boolean isHitByExplosion(Vector3f location) {
    BoundingBox loc_boundingBox = new BoundingBox(this.getRotationBoundingBox());
    loc_boundingBox.move(new Vector3f(location).sub(this.getPosition()));
    Explosion[][] explosions = level.getExplosionItems();
    List<Explosion> explosionList = new ArrayList<>();
    for (int i = 0; i < explosions.length; i++) {
      for (int j = 0; j < explosions[i].length; j++) {
        if (explosions[i][j] != null) explosionList.add(explosions[i][j]);
      }
    }
    for (Explosion explosion : explosionList) {
      if (loc_boundingBox.isCollidingWith(explosion.getBoundingBoxExplosion())) return true;
    }
    return false;
  }

  private boolean willBeHitByExplosion(Vector3f location) {
    BoundingBox loc_boundingBox = new BoundingBox(this.getRotationBoundingBox());
    loc_boundingBox.move(new Vector3f(location).sub(this.getPosition()));
    Map<Player, List<Bomb>> placedBombs = level.getPlacedBombs();
    Explosion[][] explosions = level.getExplosionItems();
    List<Explosion> explosionList = new ArrayList<>();
    for (int i = 0; i < explosions.length; i++) {
      for (int j = 0; j < explosions[i].length; j++) {
        if (explosions[i][j] != null) explosionList.add(explosions[i][j]);
      }
    }
    for (Explosion explosion : explosionList) {
      if (loc_boundingBox.isCollidingWith(explosion.getBoundingBoxExplosion())) return true;
    }
    for (Player player : placedBombs.keySet()) {
      for (Bomb bomb : placedBombs.get(player)) {
        int power = bomb.getPower();
        Vector2i location_bomb = convertToLayout(bomb.getPosition());
        for (int x = location_bomb.x; x <= location_bomb.x + power; x++) {
          if (layout[location_bomb.y][x] == Level.DESTROYABLE_ID || layout[location_bomb.y][x] == Level.CONSTANT_ID)
            break;
          Vector3f location_explos = convertToLevel(new Vector2i(x, location_bomb.y));
          BoundingBox boundingBox = new BoundingBox(
                  new Vector3f(location_explos.x - level.getScale(),
                          location_explos.y - level.getScale(),
                          location_explos.z - level.getScale()),
                  new Vector3f(location_explos.x + level.getScale(),
                          location_explos.y + level.getScale(),
                          location_explos.z + level.getScale())
          );
          if (loc_boundingBox.isCollidingWith(boundingBox)) return true;
        }
        for (int x = location_bomb.x; x >= location_bomb.x - power; x--) {
          if (layout[location_bomb.y][x] == Level.DESTROYABLE_ID || layout[location_bomb.y][x] == Level.CONSTANT_ID)
            break;
          Vector3f location_explos = convertToLevel(new Vector2i(x, location_bomb.y));
          BoundingBox boundingBox = new BoundingBox(
                  new Vector3f(location_explos.x - level.getScale(),
                          location_explos.y - level.getScale(),
                          location_explos.z - level.getScale()),
                  new Vector3f(location_explos.x + level.getScale(),
                          location_explos.y + level.getScale(),
                          location_explos.z + level.getScale())
          );
          if (loc_boundingBox.isCollidingWith(boundingBox)) return true;
        }
        for (int y = location_bomb.y; y <= location_bomb.y + power; y++) {
          if (layout[y][location_bomb.x] == Level.DESTROYABLE_ID || layout[y][location_bomb.x] == Level.CONSTANT_ID)
            break;
          Vector3f location_explos = convertToLevel(new Vector2i(location_bomb.x, y));
          BoundingBox boundingBox = new BoundingBox(
                  new Vector3f(location_explos.x - level.getScale(),
                          location_explos.y - level.getScale(),
                          location_explos.z - level.getScale()),
                  new Vector3f(location_explos.x + level.getScale(),
                          location_explos.y + level.getScale(),
                          location_explos.z + level.getScale())
          );
          if (loc_boundingBox.isCollidingWith(boundingBox)) return true;
        }
        for (int y = location_bomb.y; y >= location_bomb.y - power; y--) {
          if (layout[y][location_bomb.x] == Level.DESTROYABLE_ID || layout[y][location_bomb.x] == Level.CONSTANT_ID)
            break;
          Vector3f location_explos = convertToLevel(new Vector2i(location_bomb.x, y));
          BoundingBox boundingBox = new BoundingBox(
                  new Vector3f(location_explos.x - level.getScale(),
                          location_explos.y - level.getScale(),
                          location_explos.z - level.getScale()),
                  new Vector3f(location_explos.x + level.getScale(),
                          location_explos.y + level.getScale(),
                          location_explos.z + level.getScale())
          );
          if (loc_boundingBox.isCollidingWith(boundingBox)) return true;
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
  public void doCollisions(Vector3f oldPos, List<GameItem> noCollision) {
    super.doCollisions(oldPos, noCollision);
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
