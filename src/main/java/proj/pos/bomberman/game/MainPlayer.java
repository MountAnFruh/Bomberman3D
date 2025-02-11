package proj.pos.bomberman.game;

import org.joml.Vector2f;
import proj.pos.bomberman.engine.MouseInput;
import proj.pos.bomberman.engine.graphics.Camera;
import proj.pos.bomberman.engine.graphics.Scene;
import proj.pos.bomberman.engine.sound.SoundManager;

/**
 * @author Andreas Fruhwirt
 * @since 03.06.2018
 */
public class MainPlayer extends Player {

  private static final float MOUSE_SENSITIVITY = 0.2f;

  private final Camera camera;
  private Minimap minimap;

  private SoundManager soundManager;

  public MainPlayer(Camera camera, Level level, Scene scene) {
    super(level, scene);
    this.setScale(0.2f);
    this.camera = camera;
  }

  public void update(double delta, MouseInput mouseInput) {
    changeRotation(mouseInput);
    changePositionFromRotation();
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
  public void movePositionFromRotation(float offsetX, float offsetY, float offsetZ) {
    super.movePositionFromRotation(offsetX, offsetY, offsetZ);
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

  private void changePositionFromRotation() {
    this.movePositionFromRotation(movementVec.x * speed, movementVec.y * speed, movementVec.z * speed);
  }

  private void refreshCamera() {
    camera.setPosition(position.x, position.y, position.z);
    camera.setRotation(rotation.x, rotation.y, rotation.z);
  }

  public void setMinimap(Minimap minimap) {
    this.minimap = minimap;
  }

  public Minimap getMinimap() {
    return minimap;
  }

  @Override
  public void onDeath() {
    super.onDeath();
    soundManager.playSoundSource(BombermanGame.Sounds.DEATH.name());
  }

  public void setSoundManager(SoundManager soundManager){this.soundManager = soundManager;}

  public void remAllPowerUps()
  {
    this.setBombPower(0);
    this.setMaxBombs(0);
    this.setSpeed(0.5f);
  }
}
