package proj.pos.bomberman.game;

import org.joml.Vector3f;
import proj.pos.bomberman.engine.GameItem;
import proj.pos.bomberman.engine.graphics.Mesh;
import proj.pos.bomberman.engine.graphics.Scene;

import java.util.List;
import java.util.Random;

public class EnemyPlayer extends Player {

  private static Random rand = new Random();

  public EnemyPlayer(Mesh mesh, Level level, Scene scene) {
    super(mesh, level, scene);
    this.setScale(0.1f);
    this.mesh = mesh;
  }

  @Override
  public void update(double delta) {
    this.getMovementVec().set(rand.nextInt(3)-1, rand.nextInt(2), rand.nextInt(3)-1);
    this.moveRotation(rand.nextInt(21)-10, rand.nextInt(21)-10, rand.nextInt(21)-10);
    super.update(delta);
  }

  @Override
  public void doCollisions(Vector3f oldPos, Vector3f currentPos, List<GameItem> noCollision) {
    //super.doCollisions(oldPos, currentPos, noCollision);
  }

  @Override
  public void movePosition(float offsetX, float offsetY, float offsetZ) {
    super.movePosition(offsetX, offsetY, offsetZ);
  }
}
