package proj.pos.bomberman.game;

import proj.pos.bomberman.engine.graphics.Mesh;
import proj.pos.bomberman.engine.graphics.Scene;

public class EnemyPlayer extends Player {

  public EnemyPlayer(Mesh mesh, Level level, Scene scene) {
    super(mesh, level, scene);
    this.setScale(0.1f);
    this.mesh = mesh;
  }

  @Override
  public void update(double delta) {
    this.getMovementVec().set(-1, 0, 0);
    this.moveRotation(0, 1f, 0);
    super.update(delta);
  }

  @Override
  public void movePosition(float offsetX, float offsetY, float offsetZ) {
    super.movePosition(offsetX, offsetY, offsetZ);
  }
}
