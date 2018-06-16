package proj.pos.bomberman.game;

import org.joml.Vector3f;
import proj.pos.bomberman.engine.graphics.*;
import proj.pos.bomberman.engine.graphics.particles.FlowParticleEmitter;
import proj.pos.bomberman.engine.graphics.particles.Particle;

import java.io.IOException;
import java.util.List;

public class Explosion {

  private final Level level;
  private final Scene scene;
  private final Minimap minimap;
  private final List<Player> players;
  private final BoundingBox bbExplosion;
  private final float scale;

  private FlowParticleEmitter emitter;
  private float timeToLive;
  private float timeLived;

  public Explosion(Level level, Scene scene, BoundingBox bbExplosion,
                   float scale, float timeToLive, List<Player> players, Minimap minimap) {
    float scaleValue = (scale * 2);
    this.minimap = minimap;
    this.level = level;
    this.scale = scale;
    this.scene = scene;
    this.players = players;
    this.bbExplosion = bbExplosion;
    this.timeToLive = timeToLive;
    try {
      Vector3f particleSpeed = new Vector3f(0, 0f, 0);
      particleSpeed.mul(0.5f);
      long ttl = 500;
      int maxParticles = 20;
      long creationPeriodMillis = 100;
      float range = 0.2f;
      Mesh partMesh = OBJLoader.loadMesh("/models/particle.obj");
      Texture texture = new Texture("/textures/particle.png");
      Material partMaterial = new Material(texture, 0.0f);
      partMesh.setMaterial(partMaterial);
      Particle particle = new Particle(partMesh, particleSpeed, ttl);
      particle.setPosition(bbExplosion.getMin().x + bbExplosion.getSize().x/2,
              level.getMoved().y + scale,
              bbExplosion.getMin().z + bbExplosion.getSize().z/2);
      particle.setScale(scaleValue);
      FlowParticleEmitter particleEmitter = new FlowParticleEmitter(scene, particle, maxParticles, creationPeriodMillis, (long)timeToLive*1_000);
      particleEmitter.setActive(true);
      particleEmitter.setPositionRndRange(range);
      particleEmitter.setSpeedRndRange(range);
      emitter = particleEmitter;
      scene.getParticleEmitters().add(particleEmitter);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public void update(double delta) {
    timeLived += delta;
    if(timeLived < timeToLive) {
      for(Player player : players) {
        if(player.getBoundingBox().isCollidingWith(bbExplosion)) {
          player.addHealth(-100);
        }
      }
    } else {
      float scaleValue = (scale * 2);
      int xLevel = (int) (emitter.getBaseParticle().getPosition().x - 0.5f / (scaleValue * 2));
      int yLevel = (int) (emitter.getBaseParticle().getPosition().z - 0.5f / (scaleValue * 2));
      level.getExplosionItems()[yLevel][xLevel] = null;
      level.getExplosionLayout()[yLevel][xLevel] = Level.EMPTY_ID;
      minimap.doDrawing();
    }
  }

  public Vector3f getPosition() {
    return new Vector3f(emitter.getBaseParticle().getPosition());
  }

  public BoundingBox getBoundingBoxExplosion() {
    return bbExplosion;
  }
}
