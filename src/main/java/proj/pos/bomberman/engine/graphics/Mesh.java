package proj.pos.bomberman.engine.graphics;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL13.*;

public class Mesh {

  private final int vaoId;

  private final List<Integer> vboIdList;

  private final int vertexCount;

  private final Texture texture;

  public Mesh(float[] positions, float[] textCoords, int[] indices, Texture texture) {
    FloatBuffer posBuffer = null;
    FloatBuffer textBuffer = null;
    IntBuffer indicesBuffer = null;
    try {
      this.texture = texture;
      vertexCount = indices.length;
      vboIdList = new ArrayList<>();

      // Create the VAO and bind to it
      vaoId = glGenVertexArrays();
      glBindVertexArray(vaoId);

      // Position VBO
      int posVboId = glGenBuffers();
      vboIdList.add(posVboId);
      posBuffer = MemoryUtil.memAllocFloat(positions.length);
      posBuffer.put(positions).flip();
      glBindBuffer(GL_ARRAY_BUFFER, posVboId);
      glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW);
      glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

      // Color VBO
      int txtVboId = glGenBuffers();
      vboIdList.add(txtVboId);
      textBuffer = MemoryUtil.memAllocFloat(textCoords.length);
      textBuffer.put(textCoords).flip();
      glBindBuffer(GL_ARRAY_BUFFER, txtVboId);
      glBufferData(GL_ARRAY_BUFFER, textBuffer, GL_STATIC_DRAW);
      glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

      // Index VBO
      int idxVboId = glGenBuffers();
      vboIdList.add(idxVboId);
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
      if (textBuffer != null) {
        MemoryUtil.memFree(textBuffer);
      }
    }
  }

  public void render() {
    // Activate first texture bank
    glActiveTexture(GL_TEXTURE0);
    // Bind the texture
    glBindTexture(GL_TEXTURE_2D, texture.getId());

    // Bind to the VAO
    glBindVertexArray(getVaoId());
    glEnableVertexAttribArray(0);
    glEnableVertexAttribArray(1);

    // Draw the vertices
    glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);

    // Restore the state
    glDisableVertexAttribArray(0);
    glDisableVertexAttribArray(1);
    glBindVertexArray(0);
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
    for(int vboId : vboIdList) {
      glDeleteBuffers(vboId);
    }

    // Delete the texture
    texture.cleanup();

    // Delete the VAO
    glBindVertexArray(0);
    glDeleteVertexArrays(vaoId);
  }
}
