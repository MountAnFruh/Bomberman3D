package proj.pos.bomberman;

import proj.pos.bomberman.engine.IGameLogic;
import proj.pos.bomberman.engine.graphics.Renderer;
import proj.pos.bomberman.engine.graphics.Window;

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;

public class DummyGame implements IGameLogic {

  private int direction = 0;

  private float color = 0.0f;

  private final Renderer renderer;

  public DummyGame() {
    this.renderer = new Renderer();
  }

  @Override
  public void init() {
    try {
      renderer.init();
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
  }

  @Override
  public void render(Window window) {
    window.setClearColor(color, color, color, 0.0f);
    renderer.render(window);
  }

  @Override
  public void cleanup() {
    renderer.cleanup();
  }
}
