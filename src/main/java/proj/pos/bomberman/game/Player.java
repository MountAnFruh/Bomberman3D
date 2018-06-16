package proj.pos.bomberman.game;

import org.joml.Vector3f;
import proj.pos.bomberman.engine.GameItem;
import proj.pos.bomberman.engine.graphics.BoundingBox;
import proj.pos.bomberman.engine.graphics.Mesh;
import proj.pos.bomberman.engine.graphics.Scene;
import proj.pos.bomberman.utils.DistanceComparator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Player extends GameItem {

  protected static final float MAXBOMBPLACECOOLDOWN = 0.5f;
  protected static final float CAMERA_POS_STEP = 0.05f;

  protected final List<GameItem> noCollision = new ArrayList<>();

  protected final Scene scene;

  protected final Level level;

  protected final Vector3f movementVec;

  protected float bombPlaceCooldown = 0.0f;

  protected int maxBombs = 1;

  protected int bombPower = 1;
  protected float timeToLive = 3f; // ungf. 3 Sekunden

  protected float speed;

  protected boolean dead;

  protected int health = 100;
  protected int maxHealth = 100;

  public Player(Mesh mesh, Level level, Scene scene) {
    super(mesh);
    this.level = level;
    this.scene = scene;
    level.addPlayer(this);
    this.movementVec = new Vector3f(0, 0, 0);
    this.speed = CAMERA_POS_STEP;
  }

  public Player(Level level, Scene scene) {
    this(null, level, scene);
  }

  public void update(double delta) {
    if (this.health > 0) {
      checkPowerupCollisions();
      if (bombPlaceCooldown > 0) {
        bombPlaceCooldown -= delta;
      } else {
        bombPlaceCooldown = 0;
      }
      for (GameItem gameItem : new ArrayList<>(noCollision)) {
        if (!gameItem.isCollidingWith(this.getBoundingBox())) {
          noCollision.remove(gameItem);
        }
      }
    } else {
      if(this.dead == false) {
        this.onDeath();
      }
    }
  }

  public void placeBomb() {
    if(this.health > 0) {
      if (level.getPlacedBombs().get(this) != null) {
        if (level.getPlacedBombs().get(this).size() >= maxBombs) {
          return;
        }
      }
      if (bombPlaceCooldown == 0) {
        Bomb bomb = level.placeBomb(this, bombPower, timeToLive);
        bomb.setRotation(0,90,0);
        if (bomb != null) {
          if (bomb.isCollidingWith(this.getBoundingBox())) {
            noCollision.add(bomb);
          }
          bombPlaceCooldown = MAXBOMBPLACECOOLDOWN;
        }
      }
    }
  }

  public void checkPowerupCollisions() {
    Iterator<GameItem> iter;
    iter = new ArrayList<>(scene.getGameItems()).iterator();
    while (iter.hasNext()) {
      GameItem gameItem = iter.next();
      if (gameItem instanceof Powerup) {
        if (gameItem.isCollidingWith(this.getBoundingBox())) {
          if (gameItem.isCollidingWith(this.getBoundingBox())) {
            pickUpPowerup((Powerup) gameItem);
          }
        }
      }
    }
  }

  public void doCollisions(Vector3f oldPos, List<GameItem> noCollision) {
    List<GameItem> collidingItems = checkCollision();
    if (collidingItems.size() == 0) {
      return;
    }
    collidingItems.sort(new DistanceComparator(oldPos));
    for(GameItem gameItem : collidingItems) {
      BoundingBox boundBox = new BoundingBox(this.getBoundingBox());
      if (boundBox.isCollidingWith(gameItem.getBoundingBox())) {
        float minDiff = Float.MAX_VALUE, value, minValue = 0;
        int axis = -1;
        for (int component = 0; component < 3; component++) {
          for (int i = 0; i <= 1; i++) {
            if (i == 0) {
              value = gameItem.getBoundingBox().getMin().get(component) - boundBox.getMax().get(component);
            } else {
              value = gameItem.getBoundingBox().getMax().get(component) - boundBox.getMin().get(component);
            }
            if (Math.abs(value) < minDiff) {
              minDiff = Math.abs(value);
              minValue = value;
              axis = component;
            }
          }
        }
        this.setPosition(position.x + (axis == 0 ? minValue : 0),
                position.y + (axis == 1 ? minValue : 0),
                position.z + (axis == 2 ? minValue : 0));
      }
    }
  }

  public List<GameItem> checkCollision() {
    BoundingBox bb = new BoundingBox(this.getBoundingBox());
    List<GameItem> collidesWith = new ArrayList<>();
    Iterator<GameItem> iter;
    iter = scene.getGameItems().iterator();
    while (iter.hasNext()) {
      GameItem gameItem = iter.next();
      if (gameItem == this) continue;
      if (gameItem instanceof Powerup) continue;
      if (noCollision.contains(gameItem)) continue;
      if (gameItem.isCollidingWith(bb)) {
        collidesWith.add(gameItem);
      }
    }
    return collidesWith;
  }

  @Override
  public float getScale() {
    return super.getScale();
  }

  @Override
  public void setScale(float scale) {
    super.setScale(scale);
  }

  @Override
  public Vector3f getPosition() {
    Vector3f pos = super.getPosition();
    return pos;
  }

  @Override
  public void setPosition(float x, float y, float z) {
    super.setPosition(x, y, z);
  }

  public void pickUpPowerup(Powerup powerup) {
    int xLevel = (int) (powerup.getPosition().x);
    int yLevel = (int) (powerup.getPosition().z);
    int type = level.getItemLayout()[yLevel][xLevel];
    if (type == Level.POWERUP_SCHNELLER_ID) {
      if (this.getSpeed() <= 0.10f) {
        this.setSpeed(this.getSpeed() + 0.01f);
      }
    } else if (type == Level.POWERUP_MEHR_BOMBEN_ID) {
      if (this.getMaxBombs() <= 5) {
        this.setMaxBombs(this.getMaxBombs() + 1);
      }
    } else if (type == Level.POWERUP_MEHR_REICHWEITE_ID) {
      if (this.getBombPower() <= 5) {
        this.setBombPower(this.getBombPower() + 1);
      }
    }
    level.removePowerup(powerup);
    if(this instanceof MainPlayer)
    {
      ((MainPlayer)this).getMinimap().powerupPickedUp(type);
    }
  }

  public void movePositionFromRotation(float offsetX, float offsetY, float offsetZ) {
    Vector3f oldPos = new Vector3f(this.getPosition());
    float positionX = position.x, positionY = position.y, positionZ = position.z;
    if (offsetZ != 0) {
      positionX += (float) Math.sin(Math.toRadians(rotation.y)) * -1.0f * offsetZ;
      positionZ += (float) Math.cos(Math.toRadians(rotation.y)) * offsetZ;
    }
    if (offsetX != 0) {
      positionX += (float) Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * offsetX;
      positionZ += (float) Math.cos(Math.toRadians(rotation.y - 90)) * offsetX;
    }
    positionY += offsetY;
    this.setPosition(positionX, positionY, positionZ);
    if (scene != null && (offsetX != 0 || offsetY != 0 || offsetZ != 0)) {
      doCollisions(oldPos, noCollision);
    }
  }

  public void movePosition(float offsetX, float offsetY, float offsetZ) {
    Vector3f oldPos = new Vector3f(this.getPosition());
    this.setPosition(position.x + offsetX, position.y + offsetY, position.z + offsetZ);
    if (scene != null && (offsetX != 0 || offsetY != 0 || offsetZ != 0)) {
      doCollisions(oldPos, noCollision);
    }
  }

  @Override
  public Vector3f getRotation() {
    return super.getRotation();
  }

  @Override
  public void setRotation(float x, float y, float z) {
    super.setRotation(x, y, z);
  }

  public void moveRotation(float offsetX, float offsetY, float offsetZ) {
    float rotationX = rotation.x, rotationY = rotation.y, rotationZ = rotation.z;
    rotationX += offsetX;
    if (rotationX > 90) rotationX = 90;
    if (rotationX < -90) rotationX = -90;
    rotationY += offsetY;
    rotationZ += offsetZ;
    this.setRotation(rotationX, rotationY, rotationZ);
  }

  public Vector3f getMovementVec() {
    return movementVec;
  }

  public void setTimeToLive(float timeToLive) {
    this.timeToLive = timeToLive;
  }

  public float getTimeToLive() {
    return timeToLive;
  }

  public void setBombPower(int bombPower) {
    this.bombPower = bombPower;
  }

  public int getBombPower() {
    return bombPower;
  }

  public void setMaxBombs(int maxBombs) {
    this.maxBombs = maxBombs;
  }

  public int getMaxBombs() {
    return maxBombs;
  }

  public void setSpeed(float speed) {
    this.speed = speed;
  }

  public float getSpeed() {
    return speed;
  }

  public void addHealth(int health) {
    this.health += health;
    if (this.health < 0) {
      this.health = 0;
    } else if (this.health > maxHealth) {
      this.health = maxHealth;
    }
  }

  public int getHealth() {
    return health;
  }

  public int getMaxHealth() {
    return maxHealth;
  }

  public boolean isDead() {
    return dead;
  }

  public void onDeath() {
    this.dead = true;
    level.getMinimap().doDrawing();
    level.removePlayer(this);
    this.setPosition(this.getPosition().x, this.getPosition().y + 4.0f, this.getPosition().z);
  }
}
