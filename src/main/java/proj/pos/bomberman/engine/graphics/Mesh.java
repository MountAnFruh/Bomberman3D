package proj.pos.bomberman.engine.graphics;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class Mesh {

  private final int vaoId;
  private final int posVboId;
  private final int idxVboId;
  private final int colVboId;
  private final int vertexCount;

  public Mesh(float[] positions, float[] colors, int[] indices) {
    FloatBuffer posBuffer = null;
    FloatBuffer colorBuffer = null;
    IntBuffer indicesBuffer = null;
    try {
      vertexCount = indices.length;

      // Create the VAO and bind to it
      vaoId = glGenVertexArrays();
      glBindVertexArray(vaoId);

      // Position VBO
      posVboId = glGenBuffers();
      posBuffer = MemoryUtil.memAllocFloat(positions.length);
      posBuffer.put(positions).flip();
      glBindBuffer(GL_ARRAY_BUFFER, posVboId);
      glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW);
      glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

      // Color VBO
      colVboId = glGenBuffers();
      colorBuffer = MemoryUtil.memAllocFloat(colors.length);
      colorBuffer.put(colors).flip();
      glBindBuffer(GL_ARRAY_BUFFER, colVboId);
      glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_STATIC_DRAW);
      glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);

      // Index VBO
      idxVboId = glGenBuffers();
      indicesBuffer = MemoryUtil.memAllocInt(indices.length);
      indicesBuffer.put(indices).flip();
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxVboId);
      glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

      // Unbind the VBO
      glBindBuffer(GL_ARRAY_BUFFER, 0);

      // Unbind the VAO
      glBindVertexArray(0);
    } finally {
      if (posBuffer != null) {
        MemoryUtil.memFree(posBuffer);
      }
      if (indicesBuffer != null) {
        MemoryUtil.memFree(indicesBuffer);
      }
      if (colorBuffer != null) {
        MemoryUtil.memFree(colorBuffer);
      }
    }
  }

  public int getVaoId() {
    return vaoId;
  }

  public int getVertexCount() {
    return vertexCount;
  }

  public void cleanup() {
    glDisableVertexAttribArray(0);

    // Delete the VBOs
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    glDeleteBuffers(posVboId);
    glDeleteBuffers(colVboId);
    glDeleteBuffers(idxVboId);

    // Delete the VAO
    glBindVertexArray(0);
    glDeleteVertexArrays(vaoId);
  }
}
