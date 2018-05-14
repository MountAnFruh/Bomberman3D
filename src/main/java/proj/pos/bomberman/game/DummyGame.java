package proj.pos.bomberman.game;

import org.joml.Vector3f;
import proj.pos.bomberman.engine.GameItem;
import proj.pos.bomberman.engine.IGameLogic;
import proj.pos.bomberman.engine.MouseInput;
import proj.pos.bomberman.engine.graphics.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class DummyGame implements IGameLogic {

  private final Renderer renderer;

  private final Camera camera;

  private float lightAngle;

  private Scene scene;

  private Player player;

  private Minimap minimap;

  public DummyGame() {
    this.renderer = new Renderer();
    this.camera = new Camera();
    lightAngle = -90;
  }

  List<GameItem> gameItemsList = new ArrayList<>();

  @Override
  public void init(Window window) {
    try {
      renderer.init(window);

      scene = new Scene();

      // Create the Mesh
      float reflectance = 1f;

      //Mesh fixBlock = OBJLoader.loadMesh("/models/Boden.obj");
      Mesh fixBlock = OBJLoader.loadMesh("/models/cube.obj");
      Texture texture = new Texture("/textures/stone.png");
      Material material = new Material(texture, reflectance);
      fixBlock.setMaterial(material);

      Mesh destBlock = OBJLoader.loadMesh("/models/cube.obj");
      texture = new Texture("/textures/brick.png");
      material = new Material(texture, reflectance);
      destBlock.setMaterial(material);

      gameItemsList = new ArrayList<>();

      Level level = LevelLoader.loadMap(0.2f, "/textures/maps/map_one.png");
      this.player = new Player(camera, level);
      level.setConstantBlockMesh(fixBlock);
      level.setDestroyableBlockMesh(destBlock);
      level.setFloorBlockMesh(fixBlock);
      level.buildMap(new Vector3f(0, -2, 0), 0.5f);
      gameItemsList.addAll(level.getGameItemMap());

      List<Vector3f> spawnPoints = level.getSpawnPoints();
      Vector3f firstSpawnpoint = spawnPoints.get(0);
      player.setPosition(firstSpawnpoint.x, firstSpawnpoint.y, firstSpawnpoint.z);

      GameItem[] gameItems = gameItemsList.toArray(new GameItem[0]);
      scene.setGameItems(gameItems);

      SceneLight sceneLight = new SceneLight();
      scene.setSceneLight(sceneLight);

      // Ambient Light
      Vector3f ambientLight = new Vector3f(1f, 1f, 1f); // 0.3f
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
      skyBox.setScale(20.0f);
      scene.setSkyBox(skyBox);

      // Create Hud
      minimap = new Minimap(level/*, fixBlock, destBlock*/);

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
    } else if (window.isKeyPressed(GLFW_KEY_ENTER)) {
      setBomb();
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
    minimap.updateSize(window);
    renderer.render(window, camera, scene, minimap);
  }

  @Override
  public void cleanup() {
    renderer.cleanup();
    Map<Mesh, List<GameItem>> mapMeshes = scene.getGameMeshes();
    for (Mesh mesh : mapMeshes.keySet()) {
      mesh.cleanup();
    }
    minimap.cleanup();
  }

  public void setBomb() {
    try {
      Vector3f pos = new Vector3f(player.getPosition());
      System.out.println(pos.x);
      pos.x = pos.x + 1;
      pos.z = pos.z + 1;
      System.out.println(pos.x);
      float reflectance = 1f;
      Mesh fixBlock = null;
      fixBlock = OBJLoader.loadMesh("/models/bomb.obj");
      Texture texture = new Texture("/textures/bomb.png");
      Material material = new Material(texture, reflectance);

      fixBlock.setMaterial(material);

      GameItem gameItem = new GameItem(fixBlock);
      gameItem.setPosition(pos.x, pos.y, pos.z);
      gameItemsList.add(gameItem);
      GameItem[] gameItems = gameItemsList.toArray(new GameItem[0]);

      scene.setGameItems(gameItems);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
