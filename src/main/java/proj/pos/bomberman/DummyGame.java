package proj.pos.bomberman;

import proj.pos.bomberman.engine.GameItem;
import proj.pos.bomberman.engine.IGameLogic;
import proj.pos.bomberman.engine.graphics.Mesh;
import proj.pos.bomberman.engine.graphics.Renderer;
import proj.pos.bomberman.engine.graphics.Texture;
import proj.pos.bomberman.engine.graphics.Window;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;

public class DummyGame implements IGameLogic {

  private final Renderer renderer;

  private int direction = 0;

  private float color = 0.0f;

  private GameItem[] gameItems;

  public DummyGame() {
    this.renderer = new Renderer();
  }

  @Override
  public void init() {
    try {
      renderer.init();
      // Create the Mesh
      float[] positions = new float[] {
              // V0
              -0.5f, 0.5f, 0.5f,
              // V1
              -0.5f, -0.5f, 0.5f,
              // V2
              0.5f, -0.5f, 0.5f,
              // V3
              0.5f, 0.5f, 0.5f,
              // V4
              -0.5f, 0.5f, -0.5f,
              // V5
              0.5f, 0.5f, -0.5f,
              // V6
              -0.5f, -0.5f, -0.5f,
              // V7
              0.5f, -0.5f, -0.5f,

              // For text coords in top face
              // V8: V4 repeated
              -0.5f, 0.5f, -0.5f,
              // V9: V5 repeated
              0.5f, 0.5f, -0.5f,
              // V10: V0 repeated
              -0.5f, 0.5f, 0.5f,
              // V11: V3 repeated
              0.5f, 0.5f, 0.5f,

              // For text coords in right face
              // V12: V3 repeated
              0.5f, 0.5f, 0.5f,
              // V13: V2 repeated
              0.5f, -0.5f, 0.5f,

              // For text coords in left face
              // V14: V0 repeated
              -0.5f, 0.5f, 0.5f,
              // V15: V1 repeated
              -0.5f, -0.5f, 0.5f,

              // For text coords in bottom face
              // V16: V6 repeated
              -0.5f, -0.5f, -0.5f,
              // V17: V7 repeated
              0.5f, -0.5f, -0.5f,
              // V18: V1 repeated
              -0.5f, -0.5f, 0.5f,
              // V19: V2 repeated
              0.5f, -0.5f, 0.5f,
      };
      float[] textCoords = new float[]{
              0.0f, 0.0f,
              0.0f, 0.5f,
              0.5f, 0.5f,
              0.5f, 0.0f,

              0.0f, 0.0f,
              0.5f, 0.0f,
              0.0f, 0.5f,
              0.5f, 0.5f,

              // For text coords in top face
              0.0f, 0.5f,
              0.5f, 0.5f,
              0.0f, 1.0f,
              0.5f, 1.0f,

              // For text coords in right face
              0.0f, 0.0f,
              0.0f, 0.5f,

              // For text coords in left face
              0.5f, 0.0f,
              0.5f, 0.5f,

              // For text coords in bottom face
              0.5f, 0.0f,
              1.0f, 0.0f,
              0.5f, 0.5f,
              1.0f, 0.5f,
      };
      int[] indices = new int[]{
              // Front face
              0, 1, 3, 3, 1, 2,
              // Top Face
              8, 10, 11, 9, 8, 11,
              // Right face
              12, 13, 7, 5, 12, 7,
              // Left face
              14, 15, 6, 4, 14, 6,
              // Bottom face
              16, 18, 19, 17, 16, 19,
              // Back face
              4, 6, 7, 5, 4, 7,};
      Texture texture = new Texture("/textures/brick2.png");
      Mesh mesh = new Mesh(positions, textCoords, indices, texture);
      GameItem gameItem = new GameItem(mesh);
      gameItem.setPosition(0, 0, -2);
      gameItems = new GameItem[] { gameItem};
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public void input(Window window) {
    if (window.isKeyPressed(GLFW_KEY_UP)) {
      direction = 1;
    } else if (window.isKeyPressed(GLFW_KEY_DOWN)) {
      direction = -1;
    } else {
      direction = 0;
    }
  }

  @Override
  public void update(double delta) {
    color += direction * 0.01f;
    if (color > 1) {
      color = 1.0f;
    } else if (color < 0) {
      color = 0.0f;
    }
    // Update rotation angle
    for(GameItem gameItem : gameItems) {
      float rotation = gameItem.getRotation().x + 1.5f;
      if (rotation > 360) {
        rotation = 0;
      }
      gameItem.setRotation(rotation, rotation, rotation);
    }
  }

  @Override
  public void render(Window window) {
    window.setClearColor(color, color, color, 0.0f);
    renderer.render(window, gameItems);
  }

  @Override
  public void cleanup() {
    renderer.cleanup();
    for (GameItem gameItem : gameItems) {
      gameItem.getMesh().cleanup();
    }
  }
}
