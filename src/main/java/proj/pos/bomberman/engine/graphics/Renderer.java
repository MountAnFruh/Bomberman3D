package proj.pos.bomberman.engine.graphics;

import org.joml.Matrix4f;
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

  private Transformation transformation;

  private ShaderProgram shaderProgram;

  public Renderer() {
    transformation = new Transformation();
  }

  public void init() throws IOException {
    // Create shader
    shaderProgram = new ShaderProgram();
    shaderProgram.createVertexShader(Utils.loadResource("/shaders/vertex.vert"));
    shaderProgram.createFragmentShader(Utils.loadResource("/shaders/fragment.frag"));
    shaderProgram.link();

    // Create uniforms for world and projection matrices
    shaderProgram.createUniform("projectionMatrix");
    shaderProgram.createUniform("modelViewMatrix");
    shaderProgram.createUniform("texture_sampler");
  }

  public void clear() {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
  }

  public void render(Window window, Camera camera, GameItem[] gameItems) {
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

    shaderProgram.setUniform("texture_sampler", 0);

    // Render each gameItem
    for(GameItem gameItem : gameItems) {
      // Set world matrix for this item
      Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
      shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
      // Render the mesh for this game item
      gameItem.getMesh().render();
    }

    shaderProgram.unbind();
  }

  public void cleanup() {
    if (shaderProgram != null) {
      shaderProgram.cleanup();
    }
  }
}
