package proj.pos.bomberman;

import org.joml.Vector2f;
import org.joml.Vector3f;
import proj.pos.bomberman.engine.GameItem;
import proj.pos.bomberman.engine.IGameLogic;
import proj.pos.bomberman.engine.MouseInput;
import proj.pos.bomberman.engine.graphics.*;

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;

public class DummyGame implements IGameLogic {

  private static final float MOUSE_SENSITIVITY = 0.2f;
  private static final float CAMERA_POS_STEP = 0.05f;

  private final Vector3f cameraInc;

  private final Renderer renderer;

  private final Camera camera;

  private int colDirection = 0;

  private float color = 0.0f;

  private GameItem[] gameItems;

  public DummyGame() {
    this.renderer = new Renderer();
    this.camera = new Camera();
    this.cameraInc = new Vector3f(0, 0, 0);
  }

  @Override
  public void init(Window window) {
    try {
      renderer.init(window);
      // Create the Mesh
      Mesh mesh1 = OBJLoader.loadMesh("/models/bunny.obj");
      Mesh mesh2 = OBJLoader.loadMesh("/models/cube.obj");
      Texture texture = new Texture("/textures/brick2.png");
      mesh2.setTexture(texture);
      mesh1.setColor(new Vector3f(1f, 0.64f, 0f));
      GameItem gameItem1 = new GameItem(mesh1);
      GameItem gameItem2 = new GameItem(mesh2);
      gameItem1.setScale(0.5f);
      gameItem1.setPosition(0, 0, -2);
      gameItem2.setScale(2.0f);
      gameItem2.setPosition(0, 0, -2);
      gameItems = new GameItem[]{gameItem1, gameItem2};
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public void input(Window window, MouseInput mouseInput) {
    cameraInc.set(0, 0, 0);
    if (window.isKeyPressed(GLFW_KEY_W)) {
      cameraInc.z = -1;
    } else if (window.isKeyPressed(GLFW_KEY_S)) {
      cameraInc.z = 1;
    }
    if (window.isKeyPressed(GLFW_KEY_A)) {
      cameraInc.x = -1;
    } else if (window.isKeyPressed(GLFW_KEY_D)) {
      cameraInc.x = 1;
    }
    if (window.isKeyPressed(GLFW_KEY_Z)) {
      cameraInc.y = -1;
    } else if (window.isKeyPressed(GLFW_KEY_X)) {
      cameraInc.y = 1;
    }
    if (window.isKeyPressed(GLFW_KEY_UP)) {
      colDirection = 1;
    } else if (window.isKeyPressed(GLFW_KEY_DOWN)) {
      colDirection = -1;
    } else {
      colDirection = 0;
    }
  }

  @Override
  public void update(double delta, MouseInput mouseInput) {
    // Update camera position
    camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP,
            cameraInc.z * CAMERA_POS_STEP);
    // Update camera based on mouse
    if (mouseInput.isRightButtonPressed()) {
      Vector2f rotVec = mouseInput.getDisplVec();
      camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
    }
    // Update color
    color += colDirection * 0.01f;
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
    renderer.render(window, camera, gameItems);
  }

  @Override
  public void cleanup() {
    renderer.cleanup();
    for (GameItem gameItem : gameItems) {
      gameItem.getMesh().cleanup();
    }
  }
}
