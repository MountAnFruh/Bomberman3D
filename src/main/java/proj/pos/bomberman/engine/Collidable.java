package proj.pos.bomberman.engine;

import proj.pos.bomberman.engine.graphics.BoundingBox;

/**
 * @author Andreas Fruhwirt
 * @since 01.05.2018
 */
public interface Collidable {

  BoundingBox getBoundingBox();

  BoundingBox getRotationBoundingBox();

  default boolean isCollidingWith(BoundingBox bb2) {
    BoundingBox bb1 = getBoundingBox();
    return bb1.isCollidingWith(bb2);
  }
}
