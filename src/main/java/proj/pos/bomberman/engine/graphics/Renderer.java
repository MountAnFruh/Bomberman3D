package proj.pos.bomberman.engine.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import proj.pos.bomberman.Utils;
import proj.pos.bomberman.engine.GameItem;
import proj.pos.bomberman.engine.IHud;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {

  /*
   * Field of View in Radians
   */
  private static final float FOV = (float) Math.toRadians(60.0f);

  private static final float Z_NEAR = 0.01f;

  private static final float Z_FAR = 1000.0f;

  private static final int MAX_POINT_LIGHTS = 10;

  private Transformation transformation;

  private ShaderProgram sceneShaderProgram;

  private ShaderProgram hudShaderProgram;

  private final float specularPower;

  public Renderer() {
    transformation = new Transformation();
    specularPower = 10f;
  }

  public void init(Window window) throws IOException {
    setupSceneShader();
    setupHudShader();
  }

  private void setupSceneShader() throws IOException {
    // Create shader
    sceneShaderProgram = new ShaderProgram();
    sceneShaderProgram.createVertexShader(Utils.loadResource("/shaders/vertex.vert"));
    sceneShaderProgram.createFragmentShader(Utils.loadResource("/shaders/fragment.frag"));
    sceneShaderProgram.link();

    // Create uniforms for world and projection matrices
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

  public void render(Window window, Camera camera, GameItem[] gameItems,
                     SceneLight sceneLight, IHud hud) {
    clear();

    if (window.isResized()) {
      glViewport(0, 0, window.getWidth(), window.getHeight());
      window.setResized(false);
    }

    renderScene(window, camera, gameItems, sceneLight);

    renderHud(window, hud);
  }

  public void renderScene(Window window, Camera camera, GameItem[] gameItems, SceneLight sceneLight) {
    sceneShaderProgram.bind();

    // Update projection Matrix
    Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(),
            window.getHeight(), Z_NEAR, Z_FAR);
    sceneShaderProgram.setUniform("projectionMatrix", projectionMatrix);

    // Update view Matrix
    Matrix4f viewMatrix = transformation.getViewMatrix(camera);

    // Update Light Uniforms
    renderLights(viewMatrix, sceneLight);

    sceneShaderProgram.setUniform("texture_sampler", 0);
    // Render each gameItem
    for(GameItem gameItem : gameItems) {
      Mesh mesh = gameItem.getMesh();
      // Set world matrix for this item
      Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
      sceneShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
      // Render the mesh for this game item
      sceneShaderProgram.setUniform("material", mesh.getMaterial());
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

  private void renderHud(Window window, IHud hud) {
    hudShaderProgram.bind();

    Matrix4f ortho = transformation.getOrthoProjectionMatrix(0, window.getWidth(), window.getHeight(), 0);
    for (GameItem gameItem : hud.getGameItems()) {
      Mesh mesh = gameItem.getMesh();
      // Set orthographic and model matrix for this HUD item
      Matrix4f projModelMatrix = transformation.getOrthoProjModelMatrix(gameItem, ortho);
      hudShaderProgram.setUniform("projModelMatrix", projModelMatrix);
      hudShaderProgram.setUniform("color", gameItem.getMesh().getMaterial().getAmbientColor());
      hudShaderProgram.setUniform("hasTexture", gameItem.getMesh().getMaterial().isTextured() ? 1 : 0);

      // Render the mesh for this HUD item
      mesh.render();
    }

    hudShaderProgram.unbind();
  }

  public void cleanup() {
    if (sceneShaderProgram != null) {
      sceneShaderProgram.cleanup();
    }
    if (hudShaderProgram != null) {
      hudShaderProgram.cleanup();
    }
  }
}
