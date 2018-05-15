package proj.pos.bomberman.game;

import proj.pos.bomberman.engine.GameItem;
import proj.pos.bomberman.engine.graphics.Mesh;

public class Bomb extends GameItem {

  private float timeLived = 0.0f;
  private Level level;

  public Bomb(Mesh mesh, Level level) {
    super(mesh);
    this.mesh = mesh;
    this.level = level;
  }

  @Override
  public void update(double delta) {
    timeLived += delta;
    if (timeLived > 90.0f) {
      level.removeBomb(this);
    }
  }
}
