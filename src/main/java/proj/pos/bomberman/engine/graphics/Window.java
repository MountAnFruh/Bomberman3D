package proj.pos.bomberman.engine.graphics;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.Configuration;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

  private final String title;
  private int width;
  private int height;
  private boolean resized;

  // The window handle
  private long windowHandle;

  public Window(String title, int width, int height) {
    this.title = title;
    this.width = width;
    this.height = height;
    this.resized = false;
  }

  public void init() {
    // Activate Debug Mode
    Configuration.DEBUG.set(true);

    // Error callback zu System.err
    GLFWErrorCallback.createPrint(System.err).set();

    // Initialize GLFW
    if (!glfwInit()) {
      throw new IllegalStateException("Unable to initialize GLFW");
    }

    // Configure GLFW
    glfwDefaultWindowHints();
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

    windowHandle = glfwCreateWindow(width, height, title, NULL, NULL);
    if (windowHandle == NULL) {
      throw new RuntimeException("Failed to create GLFW window");
    }

    // Key callback for key press
    glfwSetKeyCallback(windowHandle, (windowHandle, key, scancode, action, mods) -> {
      if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) // wenn ESC gedrückt wird
        glfwSetWindowShouldClose(windowHandle, true);
    });

    // Resize callback for window resizing
    glfwSetFramebufferSizeCallback(windowHandle, (windowHandle, width, height) -> {
      this.width = width;
      this.height = height;
      this.setResized(true);
    });

    // Get resolution of monitor
    GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

    // Center window
    glfwSetWindowPos(windowHandle,
            (vidmode.width() - width) / 2,
            (vidmode.height() - height) / 2);

    // Make OpenGL context current
    glfwMakeContextCurrent(windowHandle);

    // Enable v-sync
    glfwSwapInterval(1);

    // Make window visible
    glfwShowWindow(windowHandle);

    // needed for usage
    GL.createCapabilities();

    // clear color
    glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

    // Enable depth test
    glEnable(GL_DEPTH_TEST);

    // Enable polygon mode
    // glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
  }

  public void update() {
    glfwSwapBuffers(windowHandle);
    glfwPollEvents();
  }

  public boolean isKeyPressed(int keyCode) {
    return glfwGetKey(windowHandle, keyCode) == GLFW_PRESS;
  }

  public boolean windowShouldClose() {
    return glfwWindowShouldClose(windowHandle);
  }

  public void setResized(boolean resized) {
    this.resized = resized;
  }

  public void setClearColor(float r, float g, float b, float a) {
    glClearColor(r, g, b, a);
  }

  public boolean isResized() {
    return resized;
  }

  public long getWindowHandle() {
    return windowHandle;
  }

  public int getHeight() {
    return height;
  }

  public int getWidth() {
    return width;
  }

  public String getTitle() {
    return title;
  }
}
