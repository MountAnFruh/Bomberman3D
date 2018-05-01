package proj.pos.bomberman.engine;

import org.joml.Vector2f;
import org.joml.Vector3f;
import proj.pos.bomberman.engine.graphics.Camera;
import proj.pos.bomberman.engine.graphics.Mesh;

public class Player extends GameItem {

  private static final float MOUSE_SENSITIVITY = 0.2f;
  private static final float CAMERA_POS_STEP = 0.05f;

  private final Camera camera;

  private final Vector3f movementVec;

  private float speed;

  public Player(Camera camera) {
    super();
    this.setScale(0.1f);
    this.camera = camera;
    this.movementVec = new Vector3f(0, 0, 0);
    this.speed = CAMERA_POS_STEP;
  }

  public void update(double delta, MouseInput mouseInput, Scene scene) {
    changePosition(scene);
    // Update camera based on mouse
    changeRotation(mouseInput);
  }

  private void changeRotation(MouseInput mouseInput) {
    Vector2f rotVec = mouseInput.getDisplVec();
    this.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
  }

  private void changePosition(Scene scene) {
    boolean collision = false;
    float moveX = 0, moveY = 0, moveZ = 0;
    Vector3f oldPos = new Vector3f(this.getPosition());
    this.movePosition(movementVec.x * speed, 0, 0);
    for (Mesh mesh : scene.getGameMeshes().keySet()) {
      for (GameItem gameItem : scene.getGameMeshes().get(mesh)) {
        if (gameItem.isCollidingWith(this.getBoundingBox())) collision = true;
      }
    }
    if (collision == false) {
      moveX = movementVec.x * speed;
    }
    collision = false;
    this.setPosition(oldPos.x, oldPos.y, oldPos.z);
    this.movePosition(0, movementVec.y * speed, 0);
    for (Mesh mesh : scene.getGameMeshes().keySet()) {
      for (GameItem gameItem : scene.getGameMeshes().get(mesh)) {
        if (gameItem.isCollidingWith(this.getBoundingBox())) collision = true;
      }
    }
    if (collision == false) {
      moveY = movementVec.y * speed;
    }
    collision = false;
    this.setPosition(oldPos.x, oldPos.y, oldPos.z);
    this.movePosition(0, 0, movementVec.z * speed);
    for (Mesh mesh : scene.getGameMeshes().keySet()) {
      for (GameItem gameItem : scene.getGameMeshes().get(mesh)) {
        if (gameItem.isCollidingWith(this.getBoundingBox())) collision = true;
      }
    }
    if (collision == false) {
      moveZ = movementVec.z * speed;
    }
    this.setPosition(oldPos.x, oldPos.y, oldPos.z);
    System.out.println(moveX + " " + moveY + " " + moveZ);
    this.movePosition(moveX, moveY, moveZ);
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

  public void movePosition(float offsetX, float offsetY, float offsetZ) {
    if (offsetZ != 0) {
      position.x += (float) Math.sin(Math.toRadians(rotation.y)) * -1.0f * offsetZ;
      position.z += (float) Math.cos(Math.toRadians(rotation.y)) * offsetZ;
    }
    if (offsetX != 0) {
      position.x += (float) Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * offsetX;
      position.z += (float) Math.cos(Math.toRadians(rotation.y - 90)) * offsetX;
    }
    position.y += offsetY;
    refreshCamera();
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
