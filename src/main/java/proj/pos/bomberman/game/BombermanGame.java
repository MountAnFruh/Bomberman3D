package proj.pos.bomberman.game;

import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.openal.AL11;
import proj.pos.bomberman.engine.GameItem;
import proj.pos.bomberman.engine.IGameLogic;
import proj.pos.bomberman.engine.MouseInput;
import proj.pos.bomberman.engine.graphics.*;
import proj.pos.bomberman.engine.graphics.particles.FlowParticleEmitter;
import proj.pos.bomberman.engine.graphics.particles.IParticleEmitter;
import proj.pos.bomberman.engine.sound.SoundBuffer;
import proj.pos.bomberman.engine.sound.SoundListener;
import proj.pos.bomberman.engine.sound.SoundManager;
import proj.pos.bomberman.engine.sound.SoundSource;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openal.AL10.AL_GAIN;
import static org.lwjgl.openal.AL10.alSourcef;

public class BombermanGame implements IGameLogic {

  private static final int ENEMYCOUNT = 3;

  private final Renderer renderer;

  private final Camera camera;

  private float lightAngle;

  private Scene scene;

  private MainPlayer player;

  private List<EnemyPlayer> enemyPlayers = new ArrayList<>();

  private Level level;

  private Minimap minimap;

  private final SoundManager soundManager;

  public static enum Sounds { MUSIC, EXPLOSION};

  public BombermanGame() {
    this.renderer = new Renderer();
    this.camera = new Camera();
    this.soundManager = new SoundManager();
    lightAngle = -90;
  }

  @Override
  public void init(Window window) {
    try {
      renderer.init(window);

      soundManager.init();

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
      Texture texture1 = new Texture("/textures/powerup_schneller.png");
      material = new Material(texture1, reflectance);
      powerupSpeed.setMaterial(material);

      Mesh powerupMehrBombs = OBJLoader.loadMesh("/models/cube.obj");
      Texture texture2 = new Texture("/textures/powerup_mehr_bomben.png");
      material = new Material(texture2, reflectance);
      powerupMehrBombs.setMaterial(material);

      Mesh powerupMehrReich = OBJLoader.loadMesh("/models/cube.obj");
      Texture texture3 = new Texture("/textures/powerup_mehr_reichweite.png");
      material = new Material(texture3, reflectance);
      powerupMehrReich.setMaterial(material);

      Mesh bombMesh = OBJLoader.loadMesh("/models/bomb.obj");
      Texture texture4 = new Texture("/textures/bomb.png");
      material = new Material(texture4, reflectance);
      bombMesh.setMaterial(material);

      Mesh playerMesh;

      int[][] levelLayout = LevelLoader.loadLayout(0.2f, "/textures/maps/map_one.png");
      float scaleLevel = 0.5f;
      Vector3f movedLevel = new Vector3f(0, -2, 0);
      level = new Level(levelLayout, scene, movedLevel, scaleLevel);
      this.player = new MainPlayer(camera, level, scene);
      for(int i = 0;i < ENEMYCOUNT;i++) {
        playerMesh = OBJLoader.loadMesh("/models/characterlowpoly.obj");
        material = new Material();
        material.setAmbientColor(new Vector4f(1, 0, 0, 1));
        playerMesh.setMaterial(material);
        EnemyPlayer enemyPlayer = new EnemyPlayer(playerMesh, level, scene);
        enemyPlayers.add(enemyPlayer);
      }
      level.setConstantBlockMesh(fixBlock);
      level.setDestroyableBlockMesh(destBlock);
      level.setPowerupSpeedMesh(powerupSpeed);
      level.setPowerupMehrBombMesh(powerupMehrBombs);
      level.setPowerupMehrReichMesh(powerupMehrReich);
      level.setFloorBlockMesh(fixBlock);
      level.setBombMesh(bombMesh);
      level.buildMap();

      List<Vector3f> spawnPoints = level.getSpawnPoints();
      Vector3f firstSpawnpoint = spawnPoints.get(0);
      player.setPosition(firstSpawnpoint.x, firstSpawnpoint.y, firstSpawnpoint.z);
      for(int i = 0;i < ENEMYCOUNT;i++) {
        EnemyPlayer enemyPlayer = enemyPlayers.get(i);
        Vector3f spawnpoint = spawnPoints.get((i + 1) % spawnPoints.size());
        enemyPlayer.setPosition(spawnpoint.x, spawnpoint.y, spawnpoint.z);
      }

      scene.setGameItems(level.getGameItemsLevel());

      // Create Test-Particles
      List<IParticleEmitter> particleEmitters = new ArrayList<>();
//      FlowParticleEmitter particleEmitter;
//      Vector3f particleSpeed = new Vector3f(0, 1, 0);
//      particleSpeed.mul(2.5f);
//      long ttl = 4_000;
//      int maxParticles = 200;
//      long creationPeriodMillis = 300;
//      float range = 0.2f;
//      float scale = 0.5f;
//      Mesh partMesh = OBJLoader.loadMesh("/models/particle.obj");
//      texture = new Texture("/textures/particle.png");
//      Material partMaterial = new Material(texture, reflectance);
//      partMesh.setMaterial(partMaterial);
//      Particle particle = new Particle(partMesh, particleSpeed, ttl);
//      particle.setPosition(firstSpawnpoint.x, firstSpawnpoint.y, firstSpawnpoint.z);
//      particle.setScale(scale);
//      particleEmitter = new FlowParticleEmitter(scene, particle, maxParticles, creationPeriodMillis, 0);
//      particleEmitter.setActive(true);
//      particleEmitter.setPositionRndRange(range);
//      particleEmitter.setSpeedRndRange(range);
//      particleEmitters.add(particleEmitter);
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
      minimap = new Minimap(level, movedLevel, scaleLevel, player, enemyPlayers);
      player.setMinimap(minimap);

      level.setMinimap(minimap);

      this.soundManager.init();
      this.soundManager.setAttenuationModel(AL11.AL_EXPONENT_DISTANCE);
      setupSound();
      level.setSoundManager(soundManager);

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void setupSound() throws Exception{
    //Set Background-Music
    float newVolume = 0.1f;

    SoundBuffer bufferBackground = new SoundBuffer("/sounds/8BitDespacito.ogg");
    soundManager.addSoundBuffer(bufferBackground);
    SoundSource sourceBackground = new SoundSource(true,true);
    sourceBackground.setBuffer(bufferBackground.getBufferId());
    soundManager.addSoundSource(Sounds.MUSIC.toString(),sourceBackground);
    //Sets the Music volume
    alSourcef(sourceBackground.getSourceId(), AL_GAIN, newVolume);
    sourceBackground.play();

    //Set Bomb-explosion
    newVolume = 1f;

    SoundBuffer bufferExplosion = new SoundBuffer("/sounds/explosion.ogg");
    soundManager.addSoundBuffer(bufferExplosion);
    SoundSource sourceExplosion = new SoundSource(false, true);
    sourceExplosion.setBuffer(bufferExplosion.getBufferId());
    soundManager.addSoundSource(BombermanGame.Sounds.EXPLOSION.toString(), sourceExplosion);
    //Sets the effect volume
    alSourcef(sourceExplosion.getSourceId(), AL_GAIN, newVolume);

    soundManager.setListener(new SoundListener(new Vector3f(0,0,0)));
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

    level.update(delta);

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

    for (IParticleEmitter particleEmitter : new ArrayList<>(scene.getParticleEmitters())) {
      if (particleEmitter instanceof FlowParticleEmitter) {
        FlowParticleEmitter flowParticleEmitter = (FlowParticleEmitter) particleEmitter;
        flowParticleEmitter.update((long) (delta * 1000.0));
      }
    }

    //Update sound listener position
    soundManager.updateListenerPosition(camera);
  }

  @Override
  public void render(Window window) {
    minimap.update(window);
    renderer.render(window, camera, scene, minimap);
  }

  @Override
  public void cleanup() {
    renderer.cleanup();
    soundManager.cleanup();

    scene.cleanupAllGameItems();
    minimap.cleanup();
  }
}
