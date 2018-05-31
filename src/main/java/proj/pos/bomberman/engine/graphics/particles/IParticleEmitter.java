package proj.pos.bomberman.engine.graphics.particles;

import proj.pos.bomberman.engine.GameItem;

import java.util.List;

public interface IParticleEmitter {

//  void cleanup();

  Particle getBaseParticle();

  List<GameItem> getParticles();

}
