package proj.pos.bomberman.engine;

import proj.pos.bomberman.engine.graphics.BoundingBox;

public interface Collidable {

  BoundingBox getBoundingBox();

  BoundingBox getRotationBoundingBox();

  default boolean isCollidingWith(BoundingBox bb2) {
    BoundingBox bb1 = getBoundingBox();
    return bb1.isCollidingWith(bb2);
  }
}
