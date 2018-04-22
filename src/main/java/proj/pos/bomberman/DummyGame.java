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

  private Vector3f ambientLight;

  private float lightAngle;

  private GameItem[] gameItems;

  private PointLight[] pointLights;

  private DirectionalLight directionalLight;

  public DummyGame() {
    this.renderer = new Renderer();
    this.camera = new Camera();
    this.cameraInc = new Vector3f(0, 0, 0);
    lightAngle = -90;
  }

  @Override
  public void init(Window window) {
    try {
      renderer.init(window);
      // Create the Mesh
      float reflectance = 1f;

      Mesh mesh = OBJLoader.loadMesh("/models/cube.obj");
      Texture texture = new Texture("/textures/brick2.png");
      Material material = new Material(texture, reflectance);
      mesh.setMaterial(material);

      GameItem gameItem = new GameItem(mesh);
      gameItem.setRotation(0, 90, 0);
      gameItem.setScale(0.5f);
      gameItem.setPosition(0, 0, -2);

      gameItems = new GameItem[]{gameItem};
      ambientLight = new Vector3f(0.3f, 0.3f, 0.3f);

      Vector3f lightColor = new Vector3f(1, 1, 1);
      Vector3f lightPosition = new Vector3f(0, 0, 0);
      float lightIntensity = 1.0f;
      PointLight pointLight = new PointLight(lightColor, lightPosition, lightIntensity);
      PointLight.Attenuation att = new PointLight.Attenuation(0.0f, 0.0f, 1.0f);
      pointLight.setAttenuation(att);

      Vector3f lightColor2 = new Vector3f(0, 1, 1);
      Vector3f lightPosition2 = new Vector3f(-2, 0, -1);
      PointLight pointLight2 = new PointLight(lightColor2, lightPosition2, lightIntensity);
      PointLight.Attenuation att2 = new PointLight.Attenuation(0.0f, 0.0f, 1.0f);
      pointLight2.setAttenuation(att2);

      pointLights = new PointLight[]{pointLight, pointLight2};

      lightPosition = new Vector3f(-1, 0, 0);
      lightColor = new Vector3f(1, 1, 1);
      directionalLight = new DirectionalLight(lightColor, lightPosition, lightIntensity);

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
      for (PointLight pointLight : pointLights) {
        pointLight.getPosition().z = pointLight.getPosition().z + 0.1f;
      }
    } else if (window.isKeyPressed(GLFW_KEY_DOWN)) {
      for (PointLight pointLight : pointLights) {
        pointLight.getPosition().z = pointLight.getPosition().z - 0.1f;
      }
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

    lightAngle += 1.1f;
    if (lightAngle > 90) {
      directionalLight.setIntensity(0);
      if (lightAngle >= 360) {
        lightAngle = -90;
      }
    } else if (lightAngle <= -80 || lightAngle >= 80) {
      float factor = 1 - (float) (Math.abs(lightAngle) - 80) / 10.0f;
      directionalLight.setIntensity(factor);
      directionalLight.getColor().y = Math.max(factor, 0.9f);
      directionalLight.getColor().z = Math.max(factor, 0.5f);
    } else {
      directionalLight.setIntensity(1);
      directionalLight.getColor().x = 1;
      directionalLight.getColor().y = 1;
      directionalLight.getColor().z = 1;
    }
    double angRad = Math.toRadians(lightAngle);
    directionalLight.getDirection().x = (float) Math.sin(angRad);
    directionalLight.getDirection().y = (float) Math.cos(angRad);
  }

  @Override
  public void render(Window window) {
    renderer.render(window, camera, gameItems, ambientLight, pointLights, directionalLight);
  }

  @Override
  public void cleanup() {
    renderer.cleanup();
    for (GameItem gameItem : gameItems) {
      gameItem.getMesh().cleanup();
    }
  }
}
