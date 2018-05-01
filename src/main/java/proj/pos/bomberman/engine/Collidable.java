package proj.pos.bomberman.engine;

import proj.pos.bomberman.engine.graphics.BoundingBox;

// TODO: Collisions verbessern! Kollisionen funktionieren manchmal nicht richtig
public interface Collidable {

  BoundingBox getBoundingBox();

  default boolean isCollidingWith(BoundingBox bb2) {
    BoundingBox bb1 = getBoundingBox();
    return bb1.isCollidingWith(bb2);
  }
}
