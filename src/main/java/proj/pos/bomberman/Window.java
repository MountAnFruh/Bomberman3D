package proj.pos.bomberman;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {

  private final String title;

  // The window handle
  private long windowHandle;

  public Window(String title) {
    this.title = title;
    init();
    //loop();
  }

  private void init() {
    Configuration.DEBUG.set(true);
    // Error callback zu System.err
    GLFWErrorCallback.createPrint(System.err).set();

    // Initialize GLFW
    if(!glfwInit())
      throw new IllegalStateException("Unable to initialize GLFW");

    // Configure GLFW
    glfwDefaultWindowHints();
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

    windowHandle = glfwCreateWindow(300, 300, title, NULL, NULL);
    if(windowHandle == NULL)
      throw new RuntimeException("Failed to create GLFW window");

    // Key callback für Key pressed
    glfwSetKeyCallback(windowHandle, (windowHandle, key, scancode, action, mods) -> {
      if(key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) // wenn ESC gedrückt wird
        glfwSetWindowShouldClose(windowHandle, true);
    });

    // Get the thread stack
    try(MemoryStack stack = stackPush()) {
      IntBuffer pWidth = stack.mallocInt(1);
      IntBuffer pHeight = stack.mallocInt(1);

      // Get window size
      glfwGetWindowSize(windowHandle, pWidth, pHeight);

      // Get resolution of monitor
      GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

      // Center window
      glfwSetWindowPos(windowHandle,
              (vidmode.width() - pWidth.get(0)) / 2,
              (vidmode.height() - pHeight.get(0)) / 2);
    }

    // Make OpenGL context current
    glfwMakeContextCurrent(windowHandle);

    // Enable v-sync
    glfwSwapInterval(1);

    // Make window visible
    glfwShowWindow(windowHandle);
  }

  public boolean isKeyPressed(int keyCode) {
    return glfwGetKey(windowHandle, keyCode) == GLFW_PRESS;
  }
}
