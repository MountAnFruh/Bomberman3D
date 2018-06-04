package proj.pos.bomberman.game;

import org.joml.Vector3f;
import org.joml.Vector4f;
import proj.pos.bomberman.engine.GameItem;
import proj.pos.bomberman.engine.IGameLogic;
import proj.pos.bomberman.engine.MouseInput;
import proj.pos.bomberman.engine.graphics.*;
import proj.pos.bomberman.engine.graphics.particles.FlowParticleEmitter;
import proj.pos.bomberman.engine.graphics.particles.IParticleEmitter;
import proj.pos.bomberman.engine.graphics.particles.Particle;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class BombermanGame implements IGameLogic {

  private final Renderer renderer;

  private final Camera camera;

  private float lightAngle;

  private Scene scene;

  private MainPlayer player;

  private List<EnemyPlayer> enemyPlayers = new ArrayList<>();

  private Level level;

  private Minimap minimap;

  public BombermanGame() {
    this.renderer = new Renderer();
    this.camera = new Camera();
    lightAngle = -90;
  }

  @Override
  public void init(Window window) {
    try {
      renderer.init(window);

      scene = new Scene();

      Texture texture;
      Material material;

      float reflectance = 1f;

      // Create the Meshes
      //Mesh fixBlock = OBJLoader.loadMesh("/models/Boden.obj");
      Mesh fixBlock = OBJLoader.loadMesh("/models/cube.obj");
      texture = new Texture("/textures/stone.png");
      material = new Material(texture, reflectance);
      fixBlock.setMaterial(material);

      Mesh destBlock = OBJLoader.loadMesh("/models/cube.obj");
      texture = new Texture("/textures/brick.png");
      material = new Material(texture, reflectance);
      destBlock.setMaterial(material);

      Mesh powerupSpeed = OBJLoader.loadMesh("/models/cube.obj");
      texture = new Texture("/textures/powerup_schneller.png");
      material = new Material(texture, reflectance);
      powerupSpeed.setMaterial(material);

      Mesh bombMesh = OBJLoader.loadMesh("/models/bomb.obj");
      texture = new Texture("/textures/bomb.png");
      material = new Material(texture, reflectance);
      bombMesh.setMaterial(material);

      Mesh playerMesh = OBJLoader.loadMesh("/models/characterlowpoly.obj");
      material = new Material();
      material.setAmbientColor(new Vector4f(1, 0, 0, 1));
      playerMesh.setMaterial(material);

      int[][] levelLayout = LevelLoader.loadLayout(0.2f, "/textures/maps/map_one.png");
      float scaleLevel = 0.5f;
      Vector3f movedLevel = new Vector3f(0, -2, 0);
      level = new Level(levelLayout, movedLevel, scaleLevel);
      this.player = new MainPlayer(camera, level, scene);
      for(int i = 0;i < 3;i++) {
        EnemyPlayer enemyPlayer = new EnemyPlayer(playerMesh, level, scene);
        enemyPlayers.add(enemyPlayer);
      }
      level.setConstantBlockMesh(fixBlock);
      level.setDestroyableBlockMesh(destBlock);
      level.setPowerupSpeedMesh(powerupSpeed);
      level.setFloorBlockMesh(fixBlock);
      level.setBombMesh(bombMesh);
      level.buildMap();

      List<Vector3f> spawnPoints = level.getSpawnPoints();
      Vector3f firstSpawnpoint = spawnPoints.get(0);
      player.setPosition(firstSpawnpoint.x, firstSpawnpoint.y, firstSpawnpoint.z);
      for(int i = 0;i < enemyPlayers.size();i++) {
        EnemyPlayer enemyPlayer = enemyPlayers.get(i);
        Vector3f spawnpoint = spawnPoints.get(i + 1);
        enemyPlayer.setPosition(spawnpoint.x, spawnpoint.y, spawnpoint.z);
      }

      scene.setGameItems(level.getGameItemsLevel());

      // Create Test-Particles
      List<IParticleEmitter> particleEmitters = new ArrayList<>();
      FlowParticleEmitter particleEmitter;
      Vector3f particleSpeed = new Vector3f(0, 1, 0);
      particleSpeed.mul(2.5f);
      long ttl = 4_000;
      int maxParticles = 200;
      long creationPeriodMillis = 300;
      float range = 0.2f;
      float scale = 0.5f;
      Mesh partMesh = OBJLoader.loadMesh("/models/particle.obj");
      texture = new Texture("/textures/particle.png");
      Material partMaterial = new Material(texture, reflectance);
      partMesh.setMaterial(partMaterial);
      Particle particle = new Particle(partMesh, particleSpeed, ttl);
      particle.setPosition(firstSpawnpoint.x, firstSpawnpoint.y, firstSpawnpoint.z);
      particle.setScale(scale);
      particleEmitter = new FlowParticleEmitter(particle, maxParticles, creationPeriodMillis);
      particleEmitter.setActive(true);
      particleEmitter.setPositionRndRange(range);
      particleEmitter.setSpeedRndRange(range);
      particleEmitters.add(particleEmitter);
      scene.setParticleEmitters(particleEmitters);

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
      minimap = new Minimap(level, movedLevel, scaleLevel, player);

      level.setMinimap(minimap);

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
    if (mouseInput.isLeftButtonPressed()) {
      player.placeBomb();
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
//    List<GameItem> gameItems = level.getGameItemsLevel();
//    scene.setGameItems(gameItems);
    // Update player position
    player.update(delta, mouseInput);
    for(EnemyPlayer enemyPlayer : enemyPlayers) {
      enemyPlayer.update(delta);
    }
    minimap.rotateCompass(player.getRotation().y);

    for (GameItem gameItem : new ArrayList<>(level.getGameItemsLevel())) {
      gameItem.update(delta);
    }

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

    for (IParticleEmitter particleEmitter : scene.getParticleEmitters()) {
      if (particleEmitter instanceof FlowParticleEmitter) {
        FlowParticleEmitter flowParticleEmitter = (FlowParticleEmitter) particleEmitter;
        flowParticleEmitter.update((long) (delta * 1000.0));
      }
    }
  }

  @Override
  public void render(Window window) {
    minimap.update(window);
    renderer.render(window, camera, scene, minimap);
  }

  @Override
  public void cleanup() {
    renderer.cleanup();
    scene.cleanupAllGameItems();
    minimap.cleanup();
  }
}
