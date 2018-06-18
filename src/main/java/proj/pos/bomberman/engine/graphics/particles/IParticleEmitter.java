package proj.pos.bomberman.engine.graphics.particles;

import proj.pos.bomberman.engine.GameItem;

import java.util.List;

/**
 * @author lwjgldev (angepasst von Robert Schmölzer)
 * @since 31.05.2018
 */
public interface IParticleEmitter {

//  void cleanup();

  Particle getBaseParticle();

  List<GameItem> getParticles();

}
