package proj.pos.bomberman.engine.graphics;

import org.lwjgl.system.MemoryUtil;
import proj.pos.bomberman.Utils;

import java.io.IOException;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Renderer {

  private int vboId;
  private int vaoId;

  private ShaderProgram shaderProgram;

  public void init() throws IOException {
    shaderProgram = new ShaderProgram();
    shaderProgram.createVertexShader(Utils.loadResource("/vertex.vert"));
    shaderProgram.createFragmentShader(Utils.loadResource("/fragment.frag"));
    shaderProgram.link();

    float[] vertices = new float[]{
            0.0f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f
    };

    FloatBuffer verticesBuffer = null;
    try {
      verticesBuffer = MemoryUtil.memAllocFloat(vertices.length);
      verticesBuffer.put(vertices).flip();

      // Create the VAO and bind to it
      vaoId = glGenVertexArrays();
      glBindVertexArray(vaoId);

      // Create the VBO and bind to it
      vboId = glGenBuffers();
      glBindBuffer(GL_ARRAY_BUFFER, vboId);
      glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
      // Define structure of the data
      glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

      // Unbind the VBO
      glBindBuffer(GL_ARRAY_BUFFER, 0);

      // Unbind the VAO
      glBindVertexArray(0);
    } finally {
      if (verticesBuffer != null) {
        MemoryUtil.memFree(verticesBuffer);
      }
    }
  }

  public void clear() {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
  }

  public void render(Window window) {
    clear();

    if (window.isResized()) {
      glViewport(0, 0, window.getWidth(), window.getHeight());
      window.setResized(false);
    }

    shaderProgram.bind();

    // Bind to the VAO
    glBindVertexArray(vaoId);
    glEnableVertexAttribArray(0);

    // Draw the vertices
    glDrawArrays(GL_TRIANGLES, 0, 3);

    // Restore the state
    glDisableVertexAttribArray(0);
    glBindVertexArray(0);

    shaderProgram.unbind();
  }

  public void cleanup() {
    if (shaderProgram != null) {
      shaderProgram.cleanup();
    }

    glDisableVertexAttribArray(0);

    // Delete the VBO
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glDeleteBuffers(vboId);

    // Delete the VAO
    glBindVertexArray(0);
    glDeleteVertexArrays(vaoId);
  }
}
