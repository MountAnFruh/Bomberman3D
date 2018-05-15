package proj.pos.bomberman.game;

import org.joml.Vector2f;
import org.joml.Vector3f;
import proj.pos.bomberman.engine.GameItem;
import proj.pos.bomberman.engine.MouseInput;
import proj.pos.bomberman.engine.graphics.Camera;
import proj.pos.bomberman.engine.graphics.Scene;

public class Player extends GameItem {

  private static final float MOUSE_SENSITIVITY = 0.2f;
  private static final float CAMERA_POS_STEP = 0.05f;

  private final Level level;

  private final Camera camera;

  private final Vector3f movementVec;

  private float bombPlaceCooldown = 0.0f;

  private float speed;

  public Player(Camera camera, Level level) {
    super();
    this.setScale(0.0001f);
    this.level = level;
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
  }

  private void changeRotation(MouseInput mouseInput) {
    Vector2f rotVec = mouseInput.getDisplVec();
    this.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
  }

  private void changePosition(Scene scene) {
    this.movePosition(scene, movementVec.x * speed, movementVec.y * speed, movementVec.z * speed);
  }

  public void placeBomb() {
    if (bombPlaceCooldown == 0) {
      level.placeBomb(this);
      bombPlaceCooldown = 30.0f;
    }
  }

  public void doCollisions(Scene scene, Vector3f oldPos, Vector3f currentPos) {
    boolean collision = false;
    Vector3f newPos = new Vector3f(currentPos);
    currentPos.x = newPos.x;
    currentPos.y = oldPos.y;
    currentPos.z = oldPos.z;
    boolean moveX = false, moveY = false, moveZ = false;
    boolean moveXY = false, moveYZ = false, moveZX = false;
    for (GameItem gameItem : scene.getGameItems()) {
      if (gameItem.isCollidingWith(this.getBoundingBox())) collision = true;
    }
    if (!collision) {
      moveX = true;
    }
    collision = false;
    currentPos.x = oldPos.x;
    currentPos.y = newPos.y;
    currentPos.z = oldPos.z;
    for (GameItem gameItem : scene.getGameItems()) {
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
      if (gameItem.isCollidingWith(this.getBoundingBox())) collision = true;
    }
    if (!collision) {
      moveZX = true;
    }
    if(!moveXY && moveX && moveY) {
      moveX = false; moveY = false;
    }
    if(!moveYZ && moveY && moveZ) {
      moveY = false; moveZ = false;
    }
    if(!moveZX && moveZ && moveX) {
      moveZ = false; moveX = false;
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
    if(scene != null) {
      doCollisions(scene, oldPos, currPos);
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
}
