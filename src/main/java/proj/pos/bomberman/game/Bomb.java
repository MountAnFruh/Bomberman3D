package proj.pos.bomberman.game;

import proj.pos.bomberman.engine.GameItem;
import proj.pos.bomberman.engine.graphics.Mesh;

public class Bomb extends GameItem {

  private boolean exploded = false;
  private float timeLived = 0.0f;
  private Level level;

  private int power = 1;

  public Bomb(Mesh mesh, Level level, int power) {
    super(mesh);
    this.mesh = mesh;
    this.level = level;
    this.power = power;
  }

  @Override
  public void update(double delta) {
    timeLived += delta;
    if (timeLived > 90.0f && !exploded) {
      level.explodeBomb(this);
    }
    if(exploded) {
      level.removeBomb(this);
    }
  }

  public void setPower(int power) {
    this.power = power;
  }

  public int getPower() {
    return power;
  }

  public void setExploded(boolean exploded) {
    this.exploded = exploded;
  }

  public boolean isExploded() {
    return exploded;
  }
}
