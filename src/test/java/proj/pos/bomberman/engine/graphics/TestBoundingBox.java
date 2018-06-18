package proj.pos.bomberman.engine.graphics;

import org.junit.Test;
import proj.pos.bomberman.engine.GameItem;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;

/**
 * @author Andreas Fruhwirt
 * @since 01.05.2018
 */
public class TestBoundingBox {

  private void testCollisionOfGameItems(GameItem gameItem1, GameItem gameItem2, boolean expected) {
    BoundingBox bb1 = new BoundingBox();
    BoundingBox bb2 = new BoundingBox();
    bb1.createFromGameItem(gameItem1);
    bb2.createFromGameItem(gameItem2);

    assertEquals(expected, bb1.isCollidingWith(bb2));

    System.out.println(bb1.getSize() + " " + bb1.getMin() + " " + bb1.getMax());
    System.out.println(bb2.getSize() + " " + bb2.getMin() + " " + bb2.getMax());
  }

  @Test
  public void testBoundingBoxCollision() throws IOException {
    Window window = new Window(this.getClass().getName(), 10, 10);
    window.init();

    Mesh mesh = OBJLoader.loadMesh("/bunny.obj");

    GameItem gameItem1 = new GameItem(mesh);
    gameItem1.setPosition(0f, 0f, 0f);
    gameItem1.setRotation(0f, 0f, 0f);
    gameItem1.setScale(1.0f);

    GameItem gameItem2 = new GameItem(mesh);
    gameItem2.setPosition(0f, 0f, 2.39f);
    gameItem2.setRotation(0f, 0f, 0f);
    gameItem2.setScale(1.0f);

    testCollisionOfGameItems(gameItem1, gameItem2, true);
  }

  @Test
  public void testBoundingBoxCollisionWithRotation() throws IOException {
    Window window = new Window(this.getClass().getName(), 10, 10);
    window.init();

    Mesh mesh = OBJLoader.loadMesh("/bunny.obj");

    GameItem gameItem1 = new GameItem(mesh);
    gameItem1.setPosition(0f, 0f, 0f);
    gameItem1.setRotation(0f, 45f, 45f);
    gameItem1.setScale(1.0f);

    GameItem gameItem2 = new GameItem(mesh);
    gameItem2.setPosition(0f, 0f, 2.42f);
    gameItem2.setRotation(0f, 45f, 45f);
    gameItem2.setScale(1.0f);

    testCollisionOfGameItems(gameItem1, gameItem2, true);
  }

  @Test
  public void testBoundingBoxNoCollisionWithRotation() throws IOException {
    Window window = new Window(this.getClass().getName(), 10, 10);
    window.init();

    Mesh mesh = OBJLoader.loadMesh("/bunny.obj");

    GameItem gameItem1 = new GameItem(mesh);
    gameItem1.setPosition(0f, 0f, 0f);
    gameItem1.setRotation(0f, 45f, 45f);
    gameItem1.setScale(1.0f);

    GameItem gameItem2 = new GameItem(mesh);
    gameItem2.setPosition(0f, 0f, 2.95f);
    gameItem2.setRotation(0f, 45f, 45f);
    gameItem2.setScale(1.0f);

    testCollisionOfGameItems(gameItem1, gameItem2, false);
  }

  @Test
  public void testBoundingBoxNoCollision() throws IOException {
    Window window = new Window(this.getClass().getName(), 10, 10);
    window.init();

    Mesh mesh = OBJLoader.loadMesh("/bunny.obj");

    GameItem gameItem1 = new GameItem(mesh);
    gameItem1.setPosition(0f, 0f, 0f);
    gameItem1.setRotation(0f, 0f, 0f);
    gameItem1.setScale(1.0f);

    GameItem gameItem2 = new GameItem(mesh);
    gameItem2.setPosition(0f, 0f, 2.42f);
    gameItem2.setRotation(0f, 0f, 0f);
    gameItem2.setScale(1.0f);

    testCollisionOfGameItems(gameItem1, gameItem2, false);
  }

  @Test
  public void testBoundingBoxCollisionExact() throws IOException {
    Window window = new Window(this.getClass().getName(), 10, 10);
    window.init();

    Mesh mesh = OBJLoader.loadMesh("/bunny.obj");

    GameItem gameItem1 = new GameItem(mesh);
    gameItem1.setPosition(0f, 0f, 0f);
    gameItem1.setRotation(0f, 0f, 0f);
    gameItem1.setScale(1.0f);

    BoundingBox bb1 = new BoundingBox();
    bb1.createFromGameItem(gameItem1);

    GameItem gameItem2 = new GameItem(mesh);
    gameItem2.setPosition(0, bb1.getSize().y + 0.1f, 0);
    gameItem2.setRotation(0f, 0f, 0f);
    gameItem2.setScale(1.0f);

    testCollisionOfGameItems(gameItem1, gameItem2, false);

    gameItem2.setPosition(bb1.getSize().x + 0.1f, 0, 0);
    testCollisionOfGameItems(gameItem1, gameItem2, false);

    gameItem2.setPosition(0, 0, bb1.getSize().z + 0.1f);
    testCollisionOfGameItems(gameItem1, gameItem2, false);
  }

  @Test
  public void testBoundingBoxNoCollisionExact() throws IOException {
    Window window = new Window(this.getClass().getName(), 10, 10);
    window.init();

    Mesh mesh = OBJLoader.loadMesh("/bunny.obj");

    GameItem gameItem1 = new GameItem(mesh);
    gameItem1.setPosition(0f, 0f, 0f);
    gameItem1.setRotation(0f, 0f, 0f);
    gameItem1.setScale(1.0f);

    BoundingBox bb1 = new BoundingBox();
    bb1.createFromGameItem(gameItem1);

    GameItem gameItem2 = new GameItem(mesh);
    gameItem2.setPosition(0, bb1.getSize().y, 0);
    gameItem2.setRotation(0f, 0f, 0f);
    gameItem2.setScale(1.0f);

    testCollisionOfGameItems(gameItem1, gameItem2, true);

    gameItem2.setPosition(bb1.getSize().x, 0, 0);
    testCollisionOfGameItems(gameItem1, gameItem2, true);

    gameItem2.setPosition(0, 0, bb1.getSize().z);
    testCollisionOfGameItems(gameItem1, gameItem2, true);
  }

  @Test
  public void testBoundingBoxWithCubeMesh() throws IOException {
    Window window = new Window(this.getClass().getName(), 10, 10);
    window.init();

    Mesh mesh = OBJLoader.loadMesh("/cube.obj");

    GameItem gameItem1 = new GameItem(mesh);
    gameItem1.setPosition(0f, 0f, 0f);
    gameItem1.setRotation(0f, 0f, 0f);
    gameItem1.setScale(1.0f);

    GameItem gameItem2 = new GameItem(mesh);
    gameItem2.setPosition(0, 0, 0);
    gameItem2.setRotation(0f, 0f, 0f);
    gameItem2.setScale(0.5f);

    testCollisionOfGameItems(gameItem1, gameItem2, true);
  }

  @Test
  public void testBoundingBoxWithoutMesh() {
    Window window = new Window(this.getClass().getName(), 10, 10);
    window.init();

    GameItem gameItem1 = new GameItem();
    gameItem1.setPosition(0f, 0f, 0f);
    gameItem1.setRotation(0f, 0f, 0f);
    gameItem1.setScale(1.0f);

    GameItem gameItem2 = new GameItem();
    gameItem2.setPosition(0, 0, 0);
    gameItem2.setRotation(45f, 45f, 45f);
    gameItem2.setScale(0.5f);

    testCollisionOfGameItems(gameItem1, gameItem2, true);
  }
}
