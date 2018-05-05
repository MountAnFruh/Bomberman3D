package proj.pos.bomberman.engine.graphics;

import org.joml.Matrix4f;
import org.junit.Test;
import proj.pos.bomberman.engine.GameItem;
import proj.pos.bomberman.utils.Transformation;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;

public class TestTransformation {

  private static final Transformation transformation = Transformation.getInstance();

  @Test
  public void testWorldMatrixRight() throws IOException {
    Window window = new Window(this.getClass().getName(), 10, 10);
    window.init();

    Mesh mesh = OBJLoader.loadMesh("/bunny.obj");

    GameItem gameItem = new GameItem(mesh);
    gameItem.setPosition(5f, 4f, 3f);
    gameItem.setRotation(0f, 0f, 0f);
    gameItem.setScale(1.0f);

    Matrix4f worldMatrix = transformation.getWorldMatrix(gameItem);

    assertEquals(worldMatrix.m30(), gameItem.getPosition().x);
    assertEquals(worldMatrix.m31(), gameItem.getPosition().y);
    assertEquals(worldMatrix.m32(), gameItem.getPosition().z);
  }
}
