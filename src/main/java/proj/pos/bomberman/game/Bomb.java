package proj.pos.bomberman.game;

import org.joml.Vector3f;
import proj.pos.bomberman.engine.GameItem;
import proj.pos.bomberman.engine.graphics.*;
import proj.pos.bomberman.engine.graphics.particles.FlowParticleEmitter;
import proj.pos.bomberman.engine.graphics.particles.Particle;

import javax.xml.soap.Text;
import java.io.IOException;

/**
 * @author Robert Schmölzer
 * @since 15.05.2018
 */
public class Bomb extends GameItem {

  private final Player player;
  private final Level level;

  private boolean exploded = false;
  private float timeLived = 0.0f;
  private float timeToLive;

  private int power;

  public Bomb(Mesh mesh, Player player, Level level, int power, float timeToLive) {
    super(mesh);
    this.mesh = mesh;
    this.player = player;
    this.level = level;
    this.power = power;
    this.timeToLive = timeToLive;
  }

  @Override
  public void update(double delta) {
    //System.out.println(delta);
    //System.out.println(timeLived);
    timeLived += delta;
    //System.out.println(timeLived);
    if (timeLived > timeToLive && !exploded) {
      level.explodeBomb(this);
    }
    if(exploded) {
      level.removeBomb(player, this);
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
