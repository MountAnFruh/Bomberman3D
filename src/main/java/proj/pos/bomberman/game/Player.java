package proj.pos.bomberman.game;

import org.joml.Vector2f;
import org.joml.Vector3f;
import proj.pos.bomberman.engine.GameItem;
import proj.pos.bomberman.engine.MouseInput;
import proj.pos.bomberman.engine.graphics.Camera;
import proj.pos.bomberman.engine.graphics.Scene;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Player extends GameItem {

  private static final float MAXBOMBPLACECOOLDOWN = 5.0f;
  private static final float MOUSE_SENSITIVITY = 0.2f;
  private static final float CAMERA_POS_STEP = 0.05f;

  private final List<GameItem> noCollision = new ArrayList<>();

  private final Level level;

  private final Camera camera;

  private final Vector3f movementVec;

  private float bombPlaceCooldown = 0.0f;

  private int maxBombs = 1;

  private int bombPower = 1;
  private float timeToLive = 90f;

  private float speed;

  private boolean dead;

  public Player(Camera camera, Level level) {
    super();
    this.setScale(0.0001f);
    this.level = level;
    level.getPlayers().add(this);
    this.camera = camera;
    this.movementVec = new Vector3f(0, 0, 0);
    this.speed = CAMERA_POS_STEP;
  }

  public void update(double delta, MouseInput mouseInput, Scene scene) {
    changePosition(scene);
    // Update camera based on mouse
    changeRotation(mouseInput);
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
  }

  private void changeRotation(MouseInput mouseInput) {
    Vector2f rotVec = mouseInput.getDisplVec();
    this.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
  }

  private void changePosition(Scene scene) {
    this.movePosition(scene, movementVec.x * speed, movementVec.y * speed, movementVec.z * speed);
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

  public void doCollisions(Scene scene, Vector3f oldPos, Vector3f currentPos, List<GameItem> noCollision) {
    boolean collision = false;
    Vector3f newPos = new Vector3f(currentPos);
    currentPos.x = newPos.x;
    currentPos.y = oldPos.y;
    currentPos.z = oldPos.z;
    boolean moveX = false, moveY = false, moveZ = false;
    boolean moveXY = false, moveYZ = false, moveZX = false;
    //////////////Iterator
    Iterator<GameItem> iter = scene.getGameItems().iterator();
    while (iter.hasNext()) {
      GameItem gameItem = iter.next();
      if (noCollision.contains(gameItem)) continue;
      if (gameItem.isCollidingWith(this.getBoundingBox())) {
        if (gameItem.getName() != null && gameItem.getName().equalsIgnoreCase("powerup")) {
          if (gameItem.isCollidingWith(this.getBoundingBox())) {
            pickUpPowerup(gameItem, iter);
            continue;
          }
        } else {
          collision = true;
        }
      }
    }
    if (!collision) {
      moveX = true;
    }
    collision = false;
    currentPos.x = oldPos.x;
    currentPos.y = newPos.y;
    currentPos.z = oldPos.z;
    for (GameItem gameItem : scene.getGameItems()) {
      if (noCollision.contains(gameItem)) continue;
      if (gameItem.isCollidingWith(this.getBoundingBox())) collision = true;
    }
    if (!collision) {
      moveY = true;
    }
    collision = false;
    currentPos.x = oldPos.x;
    currentPos.y = oldPos.y;
    currentPos.z = newPos.z;
    for (GameItem gameItem : scene.getGameItems()) {
      if (noCollision.contains(gameItem)) continue;
      if (gameItem.isCollidingWith(this.getBoundingBox())) collision = true;
    }
    if (!collision) {
      moveZ = true;
    }
    collision = false;
    currentPos.x = newPos.x;
    currentPos.y = newPos.y;
    currentPos.z = oldPos.z;
    for (GameItem gameItem : scene.getGameItems()) {
      if (noCollision.contains(gameItem)) continue;
      if (gameItem.isCollidingWith(this.getBoundingBox())) collision = true;
    }
    if (!collision) {
      moveXY = true;
    }
    collision = false;
    currentPos.x = oldPos.x;
    currentPos.y = newPos.y;
    currentPos.z = newPos.z;
    for (GameItem gameItem : scene.getGameItems()) {
      if (noCollision.contains(gameItem)) continue;
      if (gameItem.isCollidingWith(this.getBoundingBox())) collision = true;
    }
    if (!collision) {
      moveYZ = true;
    }
    collision = false;
    currentPos.x = newPos.x;
    currentPos.y = oldPos.y;
    currentPos.z = newPos.z;
    for (GameItem gameItem : scene.getGameItems()) {
      if (noCollision.contains(gameItem)) continue;
      if (gameItem.isCollidingWith(this.getBoundingBox())) collision = true;
    }
    if (!collision) {
      moveZX = true;
    }
    if (!moveXY && moveX && moveY) {
      moveX = false;
      moveY = false;
    }
    if (!moveYZ && moveY && moveZ) {
      moveY = false;
      moveZ = false;
    }
    if (!moveZX && moveZ && moveX) {
      moveZ = false;
      moveX = false;
    }
    Vector3f between = new Vector3f(newPos).sub(oldPos);
    currentPos.x = oldPos.x + between.x * (moveX ? 1 : 0);
    currentPos.y = oldPos.y + between.y * (moveY ? 1 : 0);
    currentPos.z = oldPos.z + between.z * (moveZ ? 1 : 0);
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
    refreshCamera();
  }

  public void pickUpPowerup(GameItem gameItem, Iterator<GameItem> iter) {
    System.out.println("1");
    this.setSpeed(this.getSpeed() + 0.02f);
    System.out.println("2");
    //level.getPowerupLevel().remove(gameItem);
    System.out.println("3");
    level.removePowerup(gameItem, iter);
  }

  public void movePosition(Scene scene, float offsetX, float offsetY, float offsetZ) {
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
    if (scene != null) {
      doCollisions(scene, oldPos, currPos, noCollision);
    }
    refreshCamera();
  }

  public void movePosition(float offsetX, float offsetY, float offsetZ) {
    movePosition(null, offsetX, offsetY, offsetZ);
  }

  @Override
  public Vector3f getRotation() {
    return super.getRotation();
  }

  @Override
  public void setRotation(float x, float y, float z) {
    super.setRotation(x, y, z);
    refreshCamera();
  }

  public void moveRotation(float offsetX, float offsetY, float offsetZ) {
    rotation.x = rotation.x + offsetX;
    if (rotation.x > 90) rotation.x = 90;
    if (rotation.x < -90) rotation.x = -90;
    rotation.y = rotation.y + offsetY;
    rotation.z = rotation.z + offsetZ;
    refreshCamera();
  }

  private void refreshCamera() {
    camera.setPosition(position.x, position.y, position.z);
    camera.setRotation(rotation.x, rotation.y, rotation.z);
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

  public void onDeath() {
    this.dead = true;
    System.out.println("Player is now dead! " + dead);
  }
}
