package proj.pos.bomberman.utils;

import org.joml.Vector3f;
import proj.pos.bomberman.engine.GameItem;
import proj.pos.bomberman.engine.graphics.BoundingBox;

import java.util.Comparator;

public class DistanceComparator implements Comparator<GameItem> {

  private final Vector3f position;

  public DistanceComparator(Vector3f position) {
    this.position = position;
  }

  @Override
  public int compare(GameItem o1, GameItem o2) {
    BoundingBox b1 = o1.getBoundingBox();
    BoundingBox b2 = o2.getBoundingBox();

    Vector3f p1 = b1.getMin().add(b1.getMax().sub(b1.getMin()).div(2));
    Vector3f p2 = b2.getMin().add(b2.getMax().sub(b2.getMin()).div(2));
    p1 = p1.sub(position);
    p2 = p2.sub(position);

    int distance1 = (int)(Math.sqrt(Math.pow(p1.x,2) + Math.pow(p1.y,2) + Math.pow(p1.z,2)) * 1_000_000);
    int distance2 = (int)(Math.sqrt(Math.pow(p2.x,2) + Math.pow(p2.y,2) + Math.pow(p2.z,2)) * 1_000_000);

    return distance1 - distance2;
  }

}
