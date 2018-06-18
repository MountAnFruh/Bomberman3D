package proj.pos.bomberman.engine.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import proj.pos.bomberman.engine.GameItem;
import proj.pos.bomberman.utils.Transformation;

/**
 * @author lwjgldev (angepasst von Andreas Fruhwirt)
 * @since 01.05.2018
 */
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

  public BoundingBox(BoundingBox boundingBox) {
    this.min = new Vector3f(boundingBox.min);
    this.max = new Vector3f(boundingBox.max);
    this.size = new Vector3f(boundingBox.size);
  }

  public BoundingBox(Vector3f min, Vector3f max, Vector3f size) {
    this.min = min;
    this.max = max;
    this.size = size;
  }

  public BoundingBox(Vector3f min, Vector3f max) {
    this.min = min;
    this.max = max;
    this.size = new Vector3f(max).sub(min);
  }

  public void move(Vector3f movement) {
    this.min = this.min.add(movement);
    this.max = this.max.add(movement);
  }

  public void createFromGameItem(GameItem gameItem) {
    Mesh mesh = gameItem.getMesh();
    Matrix4f worldMatrix;
    float[] positions;
    if (mesh != null) {
      positions = mesh.getPositions();
      worldMatrix = transformation.getWorldMatrix(gameItem);
    } else {
      positions = new float[]{-1, -1, -1,
              1, 1, 1};
      worldMatrix = transformation.getWorldMatrixWithoutRotation(gameItem);
    }
    this.min = new Vector3f(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
    this.max = new Vector3f(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
    for (int i = 0; i < positions.length; i += 3) {
      Vector4f coordinate = new Vector4f(positions[i], positions[i + 1], positions[i + 2], 1.0f);
      coordinate.mul(worldMatrix);
      for (int u = 0; u < 3; u++) {
        if (coordinate.get(u) < min.get(u)) min.setComponent(u, coordinate.get(u));
        if (coordinate.get(u) > max.get(u)) max.setComponent(u, coordinate.get(u));
      }
    }
    this.size = new Vector3f(max).sub(min);
  }

  public void createFromGameItemFromAllRotations(GameItem gameItem) {
    Mesh mesh = gameItem.getMesh();
    if (mesh == null) {
      createFromGameItem(gameItem);
      return;
    }
    Matrix4f worldMatrix;
    float[] positions = mesh.getPositions();
    worldMatrix = transformation.getWorldMatrixWithoutRotation(gameItem);
    this.min = new Vector3f(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
    this.max = new Vector3f(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
    for (int i = 0; i < positions.length; i += 3) {
      Vector4f coordinate = new Vector4f(positions[i], positions[i + 1], positions[i + 2], 1.0f);
      coordinate.mul(worldMatrix);
      for (int u = 0; u < 3; u++) {
        for (int w = 0; w < 3; w++) {
          if (coordinate.get(w) < min.get(u)) min.setComponent(u, coordinate.get(w));
          if (coordinate.get(w) > max.get(u)) max.setComponent(u, coordinate.get(w));
        }
      }
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
