package proj.pos.bomberman.engine.graphics;

import de.matthiasmann.twl.utils.PNGDecoder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.ARBFramebufferObject.glGenerateMipmap;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author lwjgldev (angepasst von Robert Schmölzer)
 * @since 16.04.2018
 */
public class Texture {

  private final int id;

  private final int width;

  private final int height;

  public Texture(String fileName) throws IOException {
    this(Texture.class.getResourceAsStream(fileName));
  }

  public Texture(InputStream is) throws IOException {
    // Load Texture file
    PNGDecoder decoder = new PNGDecoder(is);

    this.width = decoder.getWidth();
    this.height = decoder.getHeight();

    // Load texture contents into a byte buffer
    ByteBuffer buf = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
    decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
    buf.flip();

    // Create a new OpenGL texture
    int textureId = glGenTextures();
    // Bind the texture
    glBindTexture(GL_TEXTURE_2D, textureId);

    // Tell OpenGL how to unpack the RGBA bytes. Each component is 1 byte size
    glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

    // Upload the texture data
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0,
            GL_RGBA, GL_UNSIGNED_BYTE, buf);
    // Generate Mip Level
    glGenerateMipmap(GL_TEXTURE_2D);

    this.id = textureId;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public int getId() {
    return id;
  }

  public void cleanup() {
    glDeleteTextures(id);
  }
}
