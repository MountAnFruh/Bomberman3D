package proj.pos.bomberman.game;

import org.joml.Vector3f;
import proj.pos.bomberman.engine.GameItem;
import proj.pos.bomberman.engine.graphics.Mesh;
import proj.pos.bomberman.engine.graphics.Scene;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Player extends GameItem {

  protected static final float MAXBOMBPLACECOOLDOWN = 0.5f;
  protected static final float CAMERA_POS_STEP = 0.05f;

  protected final List<GameItem> noCollision = new ArrayList<>();

  protected final Scene scene;

  protected final Level level;

  protected final Vector3f movementVec;

  protected float bombPlaceCooldown = 0.0f;

  protected int maxBombs = 1;

  protected int bombPower = 1;
  protected float timeToLive = 3f; // ungf. 3 Sekunden

  protected float speed;

  protected boolean dead;

  private int health = 100;
  private int maxHealth = 100;

  public Player(Mesh mesh, Level level, Scene scene) {
    super(mesh);
    this.level = level;
    this.scene = scene;
    level.addPlayer(this);
    this.movementVec = new Vector3f(0, 0, 0);
    this.speed = CAMERA_POS_STEP;
  }

  public Player(Level level, Scene scene) {
    this(null, level, scene);
  }

  public void update(double delta) {
    if (this.health > 0) {
      checkPowerupCollisions();
      if (bombPlaceCooldown > 0) {
        bombPlaceCooldown -= delta;
      } else {
        bombPlaceCooldown = 0;
      }
      for (GameItem gameItem : new ArrayList<>(noCollision)) {
        if (!gameItem.isCollidingWith(this.getBoundingBox())) {
          noCollision.remove(gameItem);
        }
      }
    } else {
      this.onDeath();
    }
  }

  public void placeBomb() {
    if (level.getPlacedBombs().get(this) != null) {
      if (level.getPlacedBombs().get(this).size() >= maxBombs) {
        return;
      }
    }
    if (bombPlaceCooldown == 0) {
      Bomb bomb = level.placeBomb(this, bombPower, timeToLive);
      if (bomb != null) {
        if (bomb.isCollidingWith(this.getBoundingBox())) {
          noCollision.add(bomb);
        }
        bombPlaceCooldown = MAXBOMBPLACECOOLDOWN;
      }
    }
  }

  public void checkPowerupCollisions() {
    Iterator<GameItem> iter;
    iter = new ArrayList<>(scene.getGameItems()).iterator();
    while (iter.hasNext()) {
      GameItem gameItem = iter.next();
      if (gameItem instanceof Powerup) {
        if (gameItem.isCollidingWith(this.getBoundingBox())) {
          if (gameItem.isCollidingWith(this.getBoundingBox())) {
            pickUpPowerup((Powerup) gameItem);
          }
        }
      }
    }
  }

  public void doCollisions(Vector3f oldPos, Vector3f currentPos, List<GameItem> noCollision) {
    Vector3f newPos = new Vector3f(currentPos);
    if (!checkCollision()) {
      return;
    }
    currentPos.x = oldPos.x;
    currentPos.y = oldPos.y;
    currentPos.z = oldPos.z;
//    currentPos.x = newPos.x;
//    currentPos.y = oldPos.y;
//    currentPos.z = oldPos.z;
//    boolean moveX = false, moveY = false, moveZ = false;
//    boolean moveXY = false, moveYZ = false, moveZX = false;
//    if (!checkCollision()) {
//      moveX = true;
//    }
//    currentPos.x = oldPos.x;
//    currentPos.y = newPos.y;
//    currentPos.z = oldPos.z;
//    if (!checkCollision()) {
//      moveY = true;
//    }
//    currentPos.x = oldPos.x;
//    currentPos.y = oldPos.y;
//    currentPos.z = newPos.z;
//    if (!checkCollision()) {
//      moveZ = true;
//    }
//    currentPos.x = newPos.x;
//    currentPos.y = newPos.y;
//    currentPos.z = oldPos.z;
//    if (!checkCollision()) {
//      moveXY = true;
//    }
//    currentPos.x = oldPos.x;
//    currentPos.y = newPos.y;
//    currentPos.z = newPos.z;
//    if (!checkCollision()) {
//      moveYZ = true;
//    }
//    currentPos.x = newPos.x;
//    currentPos.y = oldPos.y;
//    currentPos.z = newPos.z;
//    if (!checkCollision()) {
//      moveZX = true;
//    }
//    if (!moveXY && moveX && moveY) {
//      moveX = false;
//      moveY = false;
//    }
//    if (!moveYZ && moveY && moveZ) {
//      moveY = false;
//      moveZ = false;
//    }
//    if (!moveZX && moveZ && moveX) {
//      moveZ = false;
//      moveX = false;
//    }
//    Vector3f between = new Vector3f(newPos).sub(oldPos);
//    currentPos.x = oldPos.x + between.x * (moveX ? 1 : 0);
//    currentPos.y = oldPos.y + between.y * (moveY ? 1 : 0);
//    currentPos.z = oldPos.z + between.z * (moveZ ? 1 : 0);
  }

  public boolean checkCollision() {
    Iterator<GameItem> iter;
    iter = scene.getGameItems().iterator();
    while (iter.hasNext()) {
      GameItem gameItem = iter.next();
      if (gameItem == this) continue;
      if (gameItem instanceof Powerup) continue;
      if (noCollision.contains(gameItem)) continue;
      if (gameItem.isCollidingWith(this.getBoundingBox())) return true;
    }
    return false;
  }

  @Override
  public float getScale() {
    return super.getScale();
  }

  @Override
  public void setScale(float scale) {
    super.setScale(scale);
  }

  @Override
  public Vector3f getPosition() {
    return super.getPosition();
  }

  @Override
  public void setPosition(float x, float y, float z) {
    super.setPosition(x, y, z);
  }

  public void pickUpPowerup(Powerup powerup) {
    if (powerup.getArt().equals(Powerup.PowerupArt.SCHNELLER)) {
      this.setSpeed(this.getSpeed() + 0.02f);
    }else if (powerup.getArt().equals(Powerup.PowerupArt.MEHR_BOMBEN)) {
      this.setMaxBombs(this.getMaxBombs()+1);
    }else if (powerup.getArt().equals(Powerup.PowerupArt.MEHR_REICHWEITE)) {
      this.setBombPower(this.getBombPower()+1);
    }
    level.removePowerup(powerup);
  }

  public void movePositionFromRotation(float offsetX, float offsetY, float offsetZ) {
    Vector3f oldPos = new Vector3f(this.getPosition());
    if (offsetZ != 0) {
      position.x += (float) Math.sin(Math.toRadians(rotation.y)) * -1.0f * offsetZ;
      position.z += (float) Math.cos(Math.toRadians(rotation.y)) * offsetZ;
    }
    if (offsetX != 0) {
      position.x += (float) Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * offsetX;
      position.z += (float) Math.cos(Math.toRadians(rotation.y - 90)) * offsetX;
    }
    position.y += offsetY;
    Vector3f currPos = this.getPosition();
    if (scene != null && (offsetX != 0 || offsetY != 0 || offsetZ != 0)) {
      doCollisions(oldPos, currPos, noCollision);
    }
  }

  public void movePosition(float offsetX, float offsetY, float offsetZ) {
    Vector3f oldPos = new Vector3f(this.getPosition());
    position.x += offsetX;
    position.z += offsetZ;
    position.y += offsetY;
    Vector3f currPos = this.getPosition();
    if (scene != null && (offsetX != 0 || offsetY != 0 || offsetZ != 0)) {
      doCollisions(oldPos, currPos, noCollision);
    }
  }

  @Override
  public Vector3f getRotation() {
    return super.getRotation();
  }

  @Override
  public void setRotation(float x, float y, float z) {
    super.setRotation(x, y, z);
  }

  public void moveRotation(float offsetX, float offsetY, float offsetZ) {
    rotation.x = rotation.x + offsetX;
    if (rotation.x > 90) rotation.x = 90;
    if (rotation.x < -90) rotation.x = -90;
    rotation.y = rotation.y + offsetY;
    rotation.z = rotation.z + offsetZ;
  }

  public Vector3f getMovementVec() {
    return movementVec;
  }

  public void setTimeToLive(float timeToLive) {
    this.timeToLive = timeToLive;
  }

  public float getTimeToLive() {
    return timeToLive;
  }

  public void setBombPower(int bombPower) {
    this.bombPower = bombPower;
  }

  public int getBombPower() {
    return bombPower;
  }

  public void setMaxBombs(int maxBombs) {
    this.maxBombs = maxBombs;
  }

  public int getMaxBombs() {
    return maxBombs;
  }

  public void setSpeed(float speed) {
    this.speed = speed;
  }

  public float getSpeed() {
    return speed;
  }

  public void addHealth(int health) {
    this.health += health;
    if (this.health < 0) {
      this.health = 0;
    } else if (this.health > maxHealth) {
      this.health = maxHealth;
    }
  }

  public int getHealth() {
    return health;
  }

  public int getMaxHealth() {
    return maxHealth;
  }

  public void onDeath() {
    this.dead = true;
    System.out.println("Player is now dead! " + dead);
  }
}
