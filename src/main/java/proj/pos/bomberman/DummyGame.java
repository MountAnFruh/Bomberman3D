package proj.pos.bomberman;

import org.joml.Vector3f;
import proj.pos.bomberman.engine.*;
import proj.pos.bomberman.engine.graphics.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class DummyGame implements IGameLogic {

  private final Vector3f cameraInc;

  private final Renderer renderer;

  private final Camera camera;

  private float lightAngle;

  private Scene scene;

  private Player player;

  private Hud hud;

  public DummyGame() {
    this.renderer = new Renderer();
    this.camera = new Camera();
    this.cameraInc = new Vector3f(0, 0, 0);
    this.player = new Player(camera);
    lightAngle = -90;
  }

  @Override
  public void init(Window window) {
    try {
      renderer.init(window);

      scene = new Scene();

      // Create the Mesh
      float reflectance = 1f;

      Mesh mesh = OBJLoader.loadMesh("/models/cube.obj");
      Texture texture = new Texture("/textures/brick2.png");
      Material material = new Material(texture, reflectance);
      mesh.setMaterial(material);

      List<GameItem> gameItemsList = new ArrayList<>();

      for(int i = 0;i < 10;i++) {
        for (int j = 0; j < 10; j++) {
          GameItem gameItem = new GameItem(mesh);
          gameItem.setRotation(0, 90, 0);
          gameItem.setScale(0.5f);
          gameItem.setPosition(i, -10, -2 + j);
          gameItemsList.add(gameItem);
        }
      }

      GameItem[] gameItems = gameItemsList.toArray(new GameItem[0]);
      scene.setGameItems(gameItems);

      SceneLight sceneLight = new SceneLight();
      scene.setSceneLight(sceneLight);

      // Ambient Light
      Vector3f ambientLight = new Vector3f(.5f, .5f, .5f); // 0.3f
      sceneLight.setAmbientLight(ambientLight);

      Vector3f lightColor = new Vector3f(1, 1, 1);
      Vector3f lightPosition = new Vector3f(0, 0, 0);
      float lightIntensity = 1.0f; // 1.0f
//      PointLight pointLight = new PointLight(lightColor, lightPosition, lightIntensity);
//      PointLight.Attenuation att = new PointLight.Attenuation(0.0f, 0.0f, 1.0f);
//      pointLight.setAttenuation(att);
//      pointLight.setPosition(camera.getPosition());

      Vector3f lightColor2 = new Vector3f(0, 1, 1);
      Vector3f lightPosition2 = new Vector3f(-2, 0, -1);
      PointLight pointLight2 = new PointLight(lightColor2, lightPosition2, lightIntensity);
      PointLight.Attenuation att2 = new PointLight.Attenuation(0.0f, 0.0f, 1.0f);
      pointLight2.setAttenuation(att2);

      PointLight[] pointLights = new PointLight[]{/*pointLight,*/ pointLight2};
      sceneLight.setPointLightList(pointLights);

      lightPosition = new Vector3f(-1, 0, 0);
      lightColor = new Vector3f(1, 1, 1);
      DirectionalLight directionalLight = new DirectionalLight(lightColor, lightPosition, lightIntensity);
      sceneLight.setDirectionalLight(directionalLight);

      // Setup SkyBox
      SkyBox skyBox = new SkyBox("/models/skybox.obj", "/textures/skybox.png");
      skyBox.setScale(10.0f);
      scene.setSkyBox(skyBox);

      // Create Hud
      hud = new Hud("Bomberman3D - Dev Version");

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public void input(Window window, MouseInput mouseInput) {
    player.getMovementVec().set(0, 0, 0);
    if (window.isKeyPressed(GLFW_KEY_W)) {
      player.getMovementVec().z = -1;
    } else if (window.isKeyPressed(GLFW_KEY_S)) {
      player.getMovementVec().z = 1;
    }
    if (window.isKeyPressed(GLFW_KEY_A)) {
      player.getMovementVec().x = -1;
    } else if (window.isKeyPressed(GLFW_KEY_D)) {
      player.getMovementVec().x = 1;
    }
    if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
      player.getMovementVec().y = -1;
    } else if (window.isKeyPressed(GLFW_KEY_SPACE)) {
      player.getMovementVec().y = 1;
    }

//    if (window.isKeyPressed(GLFW_KEY_UP)) {
//      for (PointLight pointLight : pointLights) {
//        pointLight.getPosition().z = pointLight.getPosition().z + 0.1f;
//      }
//    } else if (window.isKeyPressed(GLFW_KEY_DOWN)) {
//      for (PointLight pointLight : pointLights) {
//        pointLight.getPosition().z = pointLight.getPosition().z - 0.1f;
//      }
//    }
  }

  @Override
  public void update(double delta, MouseInput mouseInput) {
    // Update player position
    player.update(delta, mouseInput, scene);

    hud.rotateCompass(player.getRotation().y);

    // Update directional light direction, intensity and color
    DirectionalLight directionalLight = scene.getSceneLight().getDirectionalLight();
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
    hud.updateSize(window);
    renderer.render(window, camera, scene, hud);
  }

  @Override
  public void cleanup() {
    renderer.cleanup();
    Map<Mesh, List<GameItem>> mapMeshes = scene.getGameMeshes();
    for (Mesh mesh : mapMeshes.keySet()) {
      mesh.cleanup();
    }
    hud.cleanup();
  }
}
