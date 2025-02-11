package proj.pos.bomberman.engine.graphics.particles;

import org.joml.Vector3f;
import proj.pos.bomberman.engine.GameItem;
import proj.pos.bomberman.engine.graphics.Scene;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author lwjgldev (angepasst von Robert Schmölzer)
 * @since 31.05.2018
 */
public class FlowParticleEmitter implements IParticleEmitter {

  private final Scene scene;

  private int maxParticles;

  private boolean active;

  private final List<GameItem> particles;

  private final Particle baseParticle;

  private long creationPeriodMillis;

  private long lastCreationTime;

  private long ttl;

  private long timeLived;

  private float speedRndRange;

  private float positionRndRange;

  private float scaleRndRange;

  public FlowParticleEmitter(Scene scene, Particle baseParticle, int maxParticles, long creationPeriodMillis, long ttl) {
    particles = new ArrayList<>();
    this.scene = scene;
    this.baseParticle = baseParticle;
    this.maxParticles = maxParticles;
    this.ttl = ttl;
    this.active = false;
    this.lastCreationTime = 0;
    this.timeLived = 0;
    this.creationPeriodMillis = creationPeriodMillis;
  }

  @Override
  public Particle getBaseParticle() {
    return baseParticle;
  }

  public long getCreationPeriodMillis() {
    return creationPeriodMillis;
  }

  public int getMaxParticles() {
    return maxParticles;
  }

  @Override
  public List<GameItem> getParticles() {
    return particles;
  }

  public float getPositionRndRange() {
    return positionRndRange;
  }

  public float getScaleRndRange() {
    return scaleRndRange;
  }

  public float getSpeedRndRange() {
    return speedRndRange;
  }

  public void setCreationPeriodMillis(long creationPeriodMillis) {
    this.creationPeriodMillis = creationPeriodMillis;
  }

  public void setMaxParticles(int maxParticles) {
    this.maxParticles = maxParticles;
  }

  public void setPositionRndRange(float positionRndRange) {
    this.positionRndRange = positionRndRange;
  }

  public void setScaleRndRange(float scaleRndRange) {
    this.scaleRndRange = scaleRndRange;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public void setSpeedRndRange(float speedRndRange) {
    this.speedRndRange = speedRndRange;
  }

  public void update(long elapsedTime) {
    long now = System.currentTimeMillis();
    timeLived += elapsedTime;

    if (lastCreationTime == 0) {
      lastCreationTime = now;
    }

    Iterator<? extends GameItem> it = particles.iterator();
    while (it.hasNext()) {
      Particle particle = (Particle) it.next();
      if (particle.updateTimeToLive(elapsedTime) < 0) {
        it.remove();
      } else {
        updatePosition(particle, elapsedTime);
      }
    }

    int length = this.getParticles().size();
    if(timeLived <= ttl || ttl == 0) {
      if (now - lastCreationTime >= this.creationPeriodMillis && length < maxParticles) {
        createParticle();
        this.lastCreationTime = now;
      }
    } else if (length == 0){
      if(scene.getParticleEmitters().contains(this)) {
        scene.getParticleEmitters().remove(this);
      }
    }
  }

  private void createParticle() {
    Particle particle = new Particle(this.getBaseParticle());
    // Add a little bit of randomness of the parrticle
    float sign = Math.random() > 0.5d ? -1.0f : 1.0f;
    float speedInc = sign * (float) Math.random() * this.speedRndRange;
    float posInc = sign * (float) Math.random() * this.positionRndRange;
    float scaleInc = sign * (float) Math.random() * this.scaleRndRange;
    particle.getPosition().add(posInc, posInc, posInc);
    particle.getSpeed().add(speedInc, speedInc, speedInc);
    particle.setScale(particle.getScale() + scaleInc);
    particles.add(particle);
  }

  /**
   * Updates a particle position
   *
   * @param particle    The particle to update
   * @param elapsedTime Elapsed time in milliseconds
   */
  public void updatePosition(Particle particle, long elapsedTime) {
    Vector3f speed = particle.getSpeed();
    float delta = elapsedTime / 1000.0f;
    float dx = speed.x * delta;
    float dy = speed.y * delta;
    float dz = speed.z * delta;
    Vector3f pos = particle.getPosition();
    particle.setPosition(pos.x + dx, pos.y + dy, pos.z + dz);
  }

//  @Override
//  public void cleanup() {
//    for (GameItem particle : getParticles()) {
//      particle.cleanup();
//    }
//  }

}
