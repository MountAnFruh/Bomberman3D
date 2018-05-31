package proj.pos.bomberman.engine.graphics.particles;

import org.joml.Vector3f;
import proj.pos.bomberman.engine.GameItem;
import proj.pos.bomberman.engine.graphics.Mesh;

public class Particle extends GameItem {

  private Vector3f speed;

  /**
   * Time to live for particle in milliseconds.
   */
  private long timeToLive;

  public Particle(Mesh mesh, Vector3f speed, long timeToLive) {
    super(mesh);
    this.speed = new Vector3f(speed);
    this.timeToLive = timeToLive;
  }

  public Particle(Particle baseParticle) {
    super(baseParticle.getMesh());
    Vector3f aux = baseParticle.getPosition();
    setPosition(aux.x, aux.y, aux.z);
    aux = baseParticle.getRotation();
    setRotation(aux.x, aux.y, aux.z);
    setScale(baseParticle.getScale());
    this.speed = new Vector3f(baseParticle.speed);
    this.timeToLive = baseParticle.getTimeToLive();
  }

  public Vector3f getSpeed() {
    return speed;
  }

  public void setSpeed(Vector3f speed) {
    this.speed = speed;
  }

  public long getTimeToLive() {
    return timeToLive;
  }

  public void setTimeToLive(long timeToLive) {
    this.timeToLive = timeToLive;
  }

  /**
   * Updates the Particle's TTL
   *
   * @param elapsedTime Elapsed Time in milliseconds
   * @return The Particle's TTL
   */
  public long updateTimeToLive(long elapsedTime) {
    this.timeToLive -= elapsedTime;
    return this.timeToLive;
  }

}
