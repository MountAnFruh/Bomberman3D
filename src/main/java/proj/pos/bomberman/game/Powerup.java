package proj.pos.bomberman.game;

import org.joml.Vector2f;
import proj.pos.bomberman.engine.GameItem;
import proj.pos.bomberman.engine.graphics.Mesh;

public class Powerup extends GameItem {

  private final Level level;
  private int art = 1;
  private Vector2f pos;

  public Powerup(Mesh mesh, Level level, int art, Vector2f pos) {
    super(mesh);
    this.mesh = mesh;
    this.level = level;
    this.art = art;
    this.pos = pos;
  }

  @Override
  public void update(double delta) {
  }

  public void setArt(int art) {
    this.art = art;
  }

  public int getArt() {
    return art;
  }

  public Vector2f getPos() {
    return pos;
  }

  public void setPos(Vector2f pos) {
    this.pos = pos;
  }
}
