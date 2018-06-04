package proj.pos.bomberman.game;

import proj.pos.bomberman.engine.graphics.Mesh;
import proj.pos.bomberman.engine.graphics.Scene;

import java.util.Random;

public class EnemyPlayer extends Player {

  private Random rand = new Random();

  public EnemyPlayer(Mesh mesh, Level level, Scene scene) {
    super(mesh, level, scene);
    this.setScale(0.1f);
    this.mesh = mesh;
  }

  @Override
  public void update(double delta) {
    this.getMovementVec().set(rand.nextInt(3)-1, rand.nextInt(3)-1, rand.nextInt(3)-1);
    this.moveRotation(rand.nextInt(721)-360, rand.nextInt(721)-360, rand.nextInt(721)-360);
    super.update(delta);
  }

  @Override
  public void movePosition(float offsetX, float offsetY, float offsetZ) {
    super.movePosition(offsetX, offsetY, offsetZ);
  }
}
