package proj.pos.bomberman.engine.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import proj.pos.bomberman.Utils;
import proj.pos.bomberman.engine.GameItem;

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

  private ShaderProgram shaderProgram;

  private float specularPower;

  public Renderer() {
    transformation = new Transformation();
    specularPower = 10f;
  }

  public void init(Window window) throws IOException {
    // Create shader
    shaderProgram = new ShaderProgram();
    shaderProgram.createVertexShader(Utils.loadResource("/shaders/vertex.vert"));
    shaderProgram.createFragmentShader(Utils.loadResource("/shaders/fragment.frag"));
    shaderProgram.link();

    // Create uniforms for world and projection matrices
    shaderProgram.createUniform("projectionMatrix");
    shaderProgram.createUniform("modelViewMatrix");
    shaderProgram.createUniform("texture_sampler");
    // Create uniform for material
    shaderProgram.createMaterialUniform("material");
    // Create lighting related uniforms
    shaderProgram.createUniform("specularPower");
    shaderProgram.createUniform("ambientLight");
    shaderProgram.createPointLightListUniform("pointLights", MAX_POINT_LIGHTS);
    shaderProgram.createDirectionalLightUniform("directionalLight");
  }

  public void clear() {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
  }

  public void render(Window window, Camera camera, GameItem[] gameItems,
                     Vector3f ambientLight, PointLight[] pointLightList, DirectionalLight directionalLight) {
    clear();

    if (window.isResized()) {
      glViewport(0, 0, window.getWidth(), window.getHeight());
      window.setResized(false);
    }

    shaderProgram.bind();

    // Update projection Matrix
    Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(),
            window.getHeight(), Z_NEAR, Z_FAR);
    shaderProgram.setUniform("projectionMatrix", projectionMatrix);

    // Update view Matrix
    Matrix4f viewMatrix = transformation.getViewMatrix(camera);

    // Update Light Uniforms
    renderLights(viewMatrix, ambientLight, pointLightList, directionalLight);

    shaderProgram.setUniform("texture_sampler", 0);
    // Render each gameItem
    for(GameItem gameItem : gameItems) {
      Mesh mesh = gameItem.getMesh();
      // Set world matrix for this item
      Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
      shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
      // Render the mesh for this game item
      shaderProgram.setUniform("material", mesh.getMaterial());
      mesh.render();
    }

    shaderProgram.unbind();
  }

  private void renderLights(Matrix4f viewMatrix, Vector3f ambientLight, PointLight[] pointLightList,
                            DirectionalLight directionalLight) {
    // Update light uniforms
    shaderProgram.setUniform("ambientLight", ambientLight);
    shaderProgram.setUniform("specularPower", specularPower);

    // Process Point Lights
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
      shaderProgram.setUniform("pointLights", currPointLight, i);
    }

    // Get a copy of the directional light object and transform its position to view coordinates
    DirectionalLight currDirLight = new DirectionalLight(directionalLight);
    Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
    dir.mul(viewMatrix);
    currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
    shaderProgram.setUniform("directionalLight", currDirLight);
  }

  public void cleanup() {
    if (shaderProgram != null) {
      shaderProgram.cleanup();
    }
  }
}
