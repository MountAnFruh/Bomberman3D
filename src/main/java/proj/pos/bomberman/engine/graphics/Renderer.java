package proj.pos.bomberman.engine.graphics;

import proj.pos.bomberman.Utils;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class Renderer {

  private ShaderProgram shaderProgram;

  public void init() throws IOException {
    shaderProgram = new ShaderProgram();
    shaderProgram.createVertexShader(Utils.loadResource("/vertex.vert"));
    shaderProgram.createFragmentShader(Utils.loadResource("/fragment.frag"));
    shaderProgram.link();
  }

  public void clear() {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
  }

  public void render(Window window, Mesh mesh) {
    clear();

    if (window.isResized()) {
      glViewport(0, 0, window.getWidth(), window.getHeight());
      window.setResized(false);
    }

    shaderProgram.bind();

    // Bind to the VAO
    glBindVertexArray(mesh.getVaoId());
    glEnableVertexAttribArray(0);
    glEnableVertexAttribArray(1);

    // Draw the vertices
    glDrawElements(GL_TRIANGLES, mesh.getVertexCount(), GL_UNSIGNED_INT, 0);

    // Restore the state
    glDisableVertexAttribArray(0);
    glBindVertexArray(0);

    shaderProgram.unbind();
  }

  public void cleanup() {
    if (shaderProgram != null) {
      shaderProgram.cleanup();
    }
  }
}
