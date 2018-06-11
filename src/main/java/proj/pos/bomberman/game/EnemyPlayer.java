package proj.pos.bomberman.game;

import org.joml.Vector3f;
import org.joml.Vector4f;
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
    if(this.health > 0) {
      MainPlayer mainPlayer = null;
      for (Player player : level.getPlayers()) {
        if (player instanceof MainPlayer) {
          mainPlayer = (MainPlayer) player;
        }
      }
      float xMovement = mainPlayer.getPosition().x - this.getPosition().x;
      float yMovement = mainPlayer.getPosition().y - this.getPosition().y;
      float zMovement = mainPlayer.getPosition().z - this.getPosition().z;
      //this.movePosition(xMovement != 0 ? 1 : 0, yMovement != 0 ? 1 : 0, zMovement != 0 ? -1 : 0);
      this.getMovementVec().set(-1,
              (yMovement != 0 ? yMovement > 0 ? 1 : -1 : 0), 0);
      float yRotation = (float) Math.toDegrees(Math.atan2(zMovement, xMovement)) + 180;
      this.setRotation(0, yRotation, 0);
      this.movePositionFromRotation(movementVec.x * speed, movementVec.y * speed, movementVec.z * speed);
      this.placeBomb();
    }
    super.update(delta);
  }

  @Override
  public void onDeath() {
    super.onDeath();
    this.getMesh().getMaterial().setAmbientColor(new Vector4f(0,0,0,1));
  }

  @Override
  public void doCollisions(Vector3f oldPos, Vector3f currentPos, List<GameItem> noCollision) {
    super.doCollisions(oldPos, currentPos, noCollision);
  }

}
