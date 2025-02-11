package proj.pos.bomberman.engine.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import proj.pos.bomberman.engine.GameItem;
import proj.pos.bomberman.engine.IHud;
import proj.pos.bomberman.engine.graphics.particles.IParticleEmitter;
import proj.pos.bomberman.game.SkyBox;
import proj.pos.bomberman.utils.Transformation;
import proj.pos.bomberman.utils.Utils;

import java.io.IOException;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author lwjgldev (angepasst von Andreas Fruhwirt)
 * @since 09.04.2018
 */
public class Renderer {

  /*
   * Field of View in Radians
   */
  private static final float FOV = (float) Math.toRadians(60.0f);

  private static final float Z_NEAR = 0.01f;

  private static final float Z_FAR = 1000.0f;

  private static final int MAX_POINT_LIGHTS = 10;

  private static final Transformation transformation = Transformation.getInstance();

  private ShaderProgram sceneShaderProgram;

  private ShaderProgram hudShaderProgram;

  private ShaderProgram skyBoxShaderProgram;

  private ShaderProgram particlesShaderProgram;

  private final float specularPower;

  public Renderer() {
    specularPower = 10f;
  }

  public void init(Window window) throws IOException {
    setupSkyBoxShader();
    setupSceneShader();
    setupParticlesShader();
    setupHudShader();
  }

  private void setupSkyBoxShader() throws IOException {
    skyBoxShaderProgram = new ShaderProgram();
    skyBoxShaderProgram.createVertexShader(Utils.loadResource("/shaders/skybox_vertex.vert"));
    skyBoxShaderProgram.createFragmentShader(Utils.loadResource("/shaders/skybox_fragment.frag"));
    skyBoxShaderProgram.link();

    skyBoxShaderProgram.createUniform("projectionMatrix");
    skyBoxShaderProgram.createUniform("modelViewMatrix");
    skyBoxShaderProgram.createUniform("texture_sampler");
    skyBoxShaderProgram.createUniform("ambientLight");
  }

  private void setupSceneShader() throws IOException {
    // Create shader
    sceneShaderProgram = new ShaderProgram();
    sceneShaderProgram.createVertexShader(Utils.loadResource("/shaders/vertex.vert"));
    sceneShaderProgram.createFragmentShader(Utils.loadResource("/shaders/fragment.frag"));
    sceneShaderProgram.link();

    // Create uniforms for modelView and projection matrices
    sceneShaderProgram.createUniform("projectionMatrix");
    sceneShaderProgram.createUniform("modelViewMatrix");
    sceneShaderProgram.createUniform("texture_sampler");
    // Create uniform for material
    sceneShaderProgram.createMaterialUniform("material");
    // Create lighting related uniforms
    sceneShaderProgram.createUniform("specularPower");
    sceneShaderProgram.createUniform("ambientLight");
    sceneShaderProgram.createPointLightListUniform("pointLights", MAX_POINT_LIGHTS);
    sceneShaderProgram.createDirectionalLightUniform("directionalLight");
  }

  private void setupParticlesShader() throws IOException {
    particlesShaderProgram = new ShaderProgram();
    particlesShaderProgram.createVertexShader(Utils.loadResource("/shaders/particles_vertex.vert"));
    particlesShaderProgram.createFragmentShader(Utils.loadResource("/shaders/particles_fragment.frag"));
    particlesShaderProgram.link();

    particlesShaderProgram.createUniform("projectionMatrix");
    particlesShaderProgram.createUniform("modelViewMatrix");
    particlesShaderProgram.createUniform("texture_sampler");
  }

  private void setupHudShader() throws IOException {
    hudShaderProgram = new ShaderProgram();
    hudShaderProgram.createVertexShader(Utils.loadResource("/shaders/hud_vertex.vert"));
    hudShaderProgram.createFragmentShader(Utils.loadResource("/shaders/hud_fragment.frag"));
    hudShaderProgram.link();

    // Create uniforms for Ortographic-model projection matrix and base color
    hudShaderProgram.createUniform("projModelMatrix");
    hudShaderProgram.createUniform("color");
    hudShaderProgram.createUniform("hasTexture");
  }

  public void clear() {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
  }

  public void render(Window window, Camera camera, Scene scene, IHud hud) {
    clear();

    if (window.isResized()) {
      glViewport(0, 0, window.getWidth(), window.getHeight());
      window.setResized(false);
    }

    // Update projection and view matrices once per render cycle
    transformation.updateProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
    transformation.updateViewMatrix(camera);

    renderScene(window, camera, scene);

    if (scene.getSkyBox() != null) {
      renderSkyBox(window, camera, scene);
    }

    renderParticles(window, camera, scene);

    if (hud != null) {
      renderHud(window, hud);
    }
  }

  public void renderScene(Window window, Camera camera, Scene scene) {
    sceneShaderProgram.bind();

    // Update projection Matrix
    Matrix4f projectionMatrix = transformation.getProjectionMatrix();
    sceneShaderProgram.setUniform("projectionMatrix", projectionMatrix);

    // Update view Matrix
    Matrix4f viewMatrix = transformation.getViewMatrix();

    // Update Light Uniforms
    renderLights(viewMatrix, scene.getSceneLight());

    sceneShaderProgram.setUniform("texture_sampler", 0);

    // Render each gameItems
    for (GameItem gameItem : scene.getGameItems()) {
      Mesh mesh = gameItem.getMesh();
      sceneShaderProgram.setUniform("material", mesh.getMaterial());
      // Set world matrix for this item
      Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(gameItem, viewMatrix);
      sceneShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
      mesh.render();
    }

    sceneShaderProgram.unbind();
  }

  private void renderLights(Matrix4f viewMatrix, SceneLight sceneLight) {
    // Update light uniforms
    sceneShaderProgram.setUniform("ambientLight", sceneLight.getAmbientLight());
    sceneShaderProgram.setUniform("specularPower", specularPower);

    // Process Point Lights
    PointLight[] pointLightList = sceneLight.getPointLightList();
    int numLights = pointLightList != null ? pointLightList.length : 0;
    for (int i = 0; i < numLights; i++) {
      // Get a copy of the light object and transform its position to view coordinates
      PointLight currPointLight = new PointLight(pointLightList[i]);
      Vector3f lightPos = currPointLight.getPosition();
      Vector4f aux = new Vector4f(lightPos, 1);
      aux.mul(viewMatrix);
      lightPos.x = aux.x;
      lightPos.y = aux.y;
      lightPos.z = aux.z;
      sceneShaderProgram.setUniform("pointLights", currPointLight, i);
    }

    // Get a copy of the directional light object and transform its position to view coordinates
    DirectionalLight currDirLight = new DirectionalLight(sceneLight.getDirectionalLight());
    Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
    dir.mul(viewMatrix);
    currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
    sceneShaderProgram.setUniform("directionalLight", currDirLight);
  }

  private void renderParticles(Window window, Camera camera, Scene scene) {
    particlesShaderProgram.bind();

    particlesShaderProgram.setUniform("texture_sampler", 0);
    Matrix4f projectionMatrix = transformation.getProjectionMatrix();
    particlesShaderProgram.setUniform("projectionMatrix", projectionMatrix);

    Matrix4f viewMatrix = transformation.getViewMatrix();
    List<IParticleEmitter> emitters = scene.getParticleEmitters();
    int numEmitters = emitters != null ? emitters.size() : 0;

    glDepthMask(false);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE);

    for (int i = 0; i < numEmitters; i++) {
      IParticleEmitter emitter = emitters.get(i);
      for (GameItem gameItem : emitter.getParticles()) {
        Mesh mesh = gameItem.getMesh();
        Matrix4f modelMatrix = transformation.buildModelMatrix(gameItem);
        viewMatrix.transpose3x3(modelMatrix);

        Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(modelMatrix, viewMatrix);
        modelViewMatrix.scale(gameItem.getScale());
        particlesShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
        mesh.render();
      }
    }

    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    glDepthMask(true);

    particlesShaderProgram.unbind();
  }

  private void renderHud(Window window, IHud hud) {
    hudShaderProgram.bind();

    Matrix4f ortho = transformation.getOrthoProjectionMatrix(0, window.getWidth(), window.getHeight(), 0);
    for (GameItem gameItem : hud.getGameItems()) {
      Mesh mesh = gameItem.getMesh();
      // Set orthographic and model matrix for this HUD item
      Matrix4f projModelMatrix = transformation.buildOrthoProjModelMatrix(gameItem, ortho);
      hudShaderProgram.setUniform("projModelMatrix", projModelMatrix);
      hudShaderProgram.setUniform("color", gameItem.getMesh().getMaterial().getAmbientColor());
      hudShaderProgram.setUniform("hasTexture", gameItem.getMesh().getMaterial().isTextured() ? 1 : 0);

      // Render the mesh for this HUD item
      mesh.render();
    }

    hudShaderProgram.unbind();
  }

  private void renderSkyBox(Window window, Camera camera, Scene scene) {
    skyBoxShaderProgram.bind();

    skyBoxShaderProgram.setUniform("texture_sampler", 0);

    // Update projection Matrix
    Matrix4f projectionMatrix = transformation.getProjectionMatrix();
    skyBoxShaderProgram.setUniform("projectionMatrix", projectionMatrix);
    SkyBox skyBox = scene.getSkyBox();
    Matrix4f viewMatrix = new Matrix4f(transformation.getViewMatrix());
    viewMatrix.m30(0);
    viewMatrix.m31(0);
    viewMatrix.m32(0);
    Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(skyBox, viewMatrix);
    skyBoxShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
    skyBoxShaderProgram.setUniform("ambientLight", scene.getSceneLight().getAmbientLight());

    scene.getSkyBox().getMesh().render();

    skyBoxShaderProgram.unbind();
  }

  public void cleanup() {
    if (skyBoxShaderProgram != null) {
      skyBoxShaderProgram.cleanup();
    }
    if (sceneShaderProgram != null) {
      sceneShaderProgram.cleanup();
    }
    if (hudShaderProgram != null) {
      hudShaderProgram.cleanup();
    }
  }
}
