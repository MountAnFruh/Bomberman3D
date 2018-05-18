package proj.pos.bomberman.game;

import proj.pos.bomberman.engine.GameItem;
import proj.pos.bomberman.engine.graphics.Mesh;

public class Bomb extends GameItem {

  private final Level level;

  private boolean exploded = false;
  private float timeLived = 0.0f;
  private float timeToLive;

  private int power;

  public Bomb(Mesh mesh, Level level, int power, float timeToLive) {
    super(mesh);
    this.mesh = mesh;
    this.level = level;
    this.power = power;
    this.timeToLive = timeToLive;
  }

  @Override
  public void update(double delta) {
    timeLived += delta;
    if (timeLived > timeToLive && !exploded) {
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

  public void setTimeToLive(float timeToLive) {
    this.timeToLive = timeToLive;
  }

  public float getTimeToLive() {
    return timeToLive;
  }

  public void setExploded(boolean exploded) {
    this.exploded = exploded;
  }

  public boolean isExploded() {
    return exploded;
  }
}
