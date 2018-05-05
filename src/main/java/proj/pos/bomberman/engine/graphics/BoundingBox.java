package proj.pos.bomberman.engine.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import proj.pos.bomberman.engine.GameItem;
import proj.pos.bomberman.utils.Transformation;

public class BoundingBox {

  private static final Transformation transformation = Transformation.getInstance();

  private Vector3f min;

  private Vector3f max;

  private Vector3f size;

  public BoundingBox() {
    this.min = new Vector3f(0, 0, 0);
    this.max = new Vector3f(0, 0, 0);
    this.size = new Vector3f(0, 0, 0);
  }

  public BoundingBox(Vector3f min, Vector3f max, Vector3f size) {
    this.min = min;
    this.max = max;
    this.size = size;
  }

  public void createFromGameItem(GameItem gameItem) {
    Mesh mesh = gameItem.getMesh();
    Matrix4f worldMatrix = transformation.getWorldMatrix(gameItem);
    float[] positions;
    if (mesh != null) {
      positions = mesh.getPositions();
    } else {
      positions = new float[]{-1, -1, -1,
              1, 1, 1};
    }
    this.min = new Vector3f(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
    this.max = new Vector3f(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
    for (int i = 0; i < positions.length; i += 3) {
      Vector4f coordinate = new Vector4f(positions[i], positions[i + 1], positions[i + 2], 1.0f);
      coordinate.mul(worldMatrix);
      if (coordinate.x < min.x) min.x = coordinate.x;
      if (coordinate.y < min.y) min.y = coordinate.y;
      if (coordinate.z < min.z) min.z = coordinate.z;
      if (coordinate.x > max.x) max.x = coordinate.x;
      if (coordinate.y > max.y) max.y = coordinate.y;
      if (coordinate.z > max.z) max.z = coordinate.z;
    }
    this.size = new Vector3f(max).sub(min);
  }

  public boolean isCollidingWith(BoundingBox bb2) {
    return (this.min.x <= bb2.max.x && this.max.x >= bb2.min.x) &&
            (this.min.y <= bb2.max.y && this.max.y >= bb2.min.y) &&
            (this.min.z <= bb2.max.z && this.max.z >= bb2.min.z);
  }

  public Vector3f getMin() {
    return min;
  }

  public void setMin(Vector3f min) {
    this.min = min;
  }

  public Vector3f getMax() {
    return max;
  }

  public void setMax(Vector3f max) {
    this.max = max;
  }

  public Vector3f getSize() {
    return size;
  }

  public void setSize(Vector3f size) {
    this.size = size;
  }
}
