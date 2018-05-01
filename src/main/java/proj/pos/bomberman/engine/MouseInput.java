package proj.pos.bomberman.engine;

import org.joml.Vector2d;
import org.joml.Vector2f;
import proj.pos.bomberman.engine.graphics.Window;

import static org.lwjgl.glfw.GLFW.*;

public class MouseInput {

  private final Vector2d currentPos;

  private final Vector2f displVec;

  private boolean inWindow = false;

  private boolean leftButtonPressed = false;

  private boolean rightButtonPressed = false;

  public MouseInput() {
    currentPos = new Vector2d(0, 0);
    displVec = new Vector2f();
  }

  public void init(Window window) {
    glfwSetCursorPosCallback(window.getWindowHandle(), (windowHandle, xpos, ypos) -> {
      currentPos.x = xpos;
      currentPos.y = ypos;
    });
    glfwSetCursorEnterCallback(window.getWindowHandle(), (windowHandle, entered) -> {
      inWindow = entered;
    });
    glfwSetMouseButtonCallback(window.getWindowHandle(), (windowHandle, button, action, mode) -> {
      leftButtonPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
      rightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
    });
  }

  public Vector2f getDisplVec() {
    return displVec;
  }

  public void input(Window window) {
    displVec.x = 0;
    displVec.y = 0;
    if (inWindow) {
      double deltaX = currentPos.x - window.getWidth() / 2;
      double deltaY = currentPos.y - window.getHeight() / 2;
      boolean rotateX = deltaX != 0;
      boolean rotateY = deltaY != 0;
      if (rotateX) {
        displVec.y = (float) deltaX;
      }
      if (rotateY) {
        displVec.x = (float) deltaY;
      }
    }
    glfwSetCursorPos(window.getWindowHandle(), window.getWidth() / 2, window.getHeight() / 2);
  }

  public boolean isLeftButtonPressed() {
    return leftButtonPressed;
  }

  public boolean isRightButtonPressed() {
    return rightButtonPressed;
  }

  public boolean isInWindow() {
    return inWindow;
  }
}
