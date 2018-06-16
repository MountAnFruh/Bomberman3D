package proj.pos.bomberman.engine;

import org.joml.Vector3f;
import proj.pos.bomberman.engine.graphics.BoundingBox;
import proj.pos.bomberman.engine.graphics.Mesh;

public class GameItem implements Collidable {

  protected BoundingBox boundingBox;

  protected BoundingBox allRotBoundingBox;

  protected Mesh mesh;

  protected Vector3f position;

  protected float scale;

  protected Vector3f rotation;

  public GameItem() {
    position = new Vector3f(0, 0, 0);
    scale = 1;
    rotation = new Vector3f(0, 0, 0);
    boundingBox = new BoundingBox();
    allRotBoundingBox = new BoundingBox();
  }

  public GameItem(Mesh mesh) {
    this();
    this.mesh = mesh;
  }

  public void update(double delta) {
    // add update for other items
  }

  public Vector3f getPosition() {
    return new Vector3f(position);
  }

  public void setPosition(float x, float y, float z) {
    this.position.x = x;
    this.position.y = y;
    this.position.z = z;
//    boundingBox.createFromGameItem(this);
//    allRotBoundingBox.createFromGameItemFromAllRotations(this);
  }

  public float getScale() {
    return scale;
  }

  public void setScale(float scale) {
    this.scale = scale;
//    boundingBox.createFromGameItem(this);
//    allRotBoundingBox.createFromGameItemFromAllRotations(this);
  }

  public Vector3f getRotation() {
    return rotation;
  }

  public void setRotation(float x, float y, float z) {
    this.rotation.x = x;
    this.rotation.y = y;
    this.rotation.z = z;
//    boundingBox.createFromGameItem(this);
  }

  public Mesh getMesh() {
    return mesh;
  }

  public boolean hasMesh() {
    return mesh != null;
  }

  public void setMesh(Mesh mesh) {
    this.mesh = mesh;
//    boundingBox.createFromGameItem(this);
//    allRotBoundingBox.createFromGameItemFromAllRotations(this);
  }

  @Override
  public BoundingBox getBoundingBox() {
    boundingBox.createFromGameItem(this);
    return boundingBox;
  }

  @Override
  public BoundingBox getRotationBoundingBox() {
    allRotBoundingBox.createFromGameItemFromAllRotations(this);
    return boundingBox;
  }
}
