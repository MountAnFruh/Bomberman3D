package proj.pos.bomberman.engine;

import org.joml.Vector3f;
import proj.pos.bomberman.engine.graphics.BoundingBox;
import proj.pos.bomberman.engine.graphics.Mesh;

public class GameItem implements Collidable {

  protected Mesh mesh;

  protected Vector3f position;

  protected float scale;

  protected Vector3f rotation;

  public GameItem() {
    position = new Vector3f(0, 0, 0);
    scale = 1;
    rotation = new Vector3f(0, 0, 0);
  }

  public GameItem(Mesh mesh) {
    this();
    this.mesh = mesh;
  }

  public void update(double delta) {
    // add update for other items
  }

  public Vector3f getPosition() {
    return position;
  }

  public void setPosition(float x, float y, float z) {
    this.position.x = x;
    this.position.y = y;
    this.position.z = z;
  }

  public float getScale() {
    return scale;
  }

  public void setScale(float scale) {
    this.scale = scale;
  }

  public Vector3f getRotation() {
    return rotation;
  }

  public void setRotation(float x, float y, float z) {
    this.rotation.x = x;
    this.rotation.y = y;
    this.rotation.z = z;
  }

  public Mesh getMesh() {
    return mesh;
  }

  public void setMesh(Mesh mesh) {
    this.mesh = mesh;
  }

  @Override
  public BoundingBox getBoundingBox() {
    BoundingBox boundingBox = new BoundingBox();
    boundingBox.createFromGameItem(this);
    return boundingBox;
  }
}
