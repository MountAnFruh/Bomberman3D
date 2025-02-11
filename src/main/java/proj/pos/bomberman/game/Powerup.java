package proj.pos.bomberman.game;

import proj.pos.bomberman.engine.GameItem;
import proj.pos.bomberman.engine.graphics.Mesh;

/**
 * @author Robert Schmölzer
 * @since 31.05.2018
 */
public class Powerup extends GameItem {

  private final Level level;
  private PowerupArt art;

  public Powerup(Mesh mesh, Level level, PowerupArt art) {
    super(mesh);
    this.mesh = mesh;
    this.level = level;
    this.art = art;
  }

  @Override
  public void update(double delta) {
    this.setRotation(this.getRotation().x, this.getRotation().y + 5, this.getRotation().z);
  }

  public void setArt(PowerupArt art) {
    this.art = art;
  }

  public PowerupArt getArt() {
    return art;
  }

  public enum PowerupArt {
    SCHNELLER, MEHR_BOMBEN, MEHR_REICHWEITE, NICHTS;
  }
}
