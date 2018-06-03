package proj.pos.bomberman.game;

import org.joml.Vector2f;
import proj.pos.bomberman.engine.MouseInput;
import proj.pos.bomberman.engine.graphics.Camera;
import proj.pos.bomberman.engine.graphics.Scene;

public class MainPlayer extends Player {

  private static final float MOUSE_SENSITIVITY = 0.2f;

  private final Camera camera;

  public MainPlayer(Camera camera, Level level, Scene scene) {
    super(level, scene);
    this.setScale(0.0001f);
    this.camera = camera;
  }

  public void update(double delta, MouseInput mouseInput) {
    changeRotation(mouseInput);
    super.update(delta);
  }

  @Override
  public void setPosition(float x, float y, float z) {
    super.setPosition(x, y, z);
    refreshCamera();
  }

  @Override
  public void movePosition(float offsetX, float offsetY, float offsetZ) {
    super.movePosition(offsetX, offsetY, offsetZ);
    refreshCamera();
  }

  @Override
  public void setRotation(float x, float y, float z) {
    super.setRotation(x, y, z);
    refreshCamera();
  }

  @Override
  public void moveRotation(float offsetX, float offsetY, float offsetZ) {
    super.moveRotation(offsetX, offsetY, offsetZ);
    refreshCamera();
  }

  private void changeRotation(MouseInput mouseInput) {
    Vector2f rotVec = mouseInput.getDisplVec();
    this.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
  }

  private void refreshCamera() {
    camera.setPosition(position.x, position.y, position.z);
    camera.setRotation(rotation.x, rotation.y, rotation.z);
  }
}
