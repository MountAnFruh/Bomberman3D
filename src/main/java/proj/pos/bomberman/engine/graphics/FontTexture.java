package proj.pos.bomberman.engine.graphics;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lwjgldev (angepasst von Nico Prosser)
 * @since 25.04.2018
 */
public class FontTexture {

  private static final String IMAGE_FORMAT = "png";

  private final Font font;

  private final String charSetName;

  private final Map<Character, CharInfo> charMap;

  private Texture texture;

  private int height;

  private int width;

  public FontTexture(Font font, String charSetName) throws IOException {
    this.font = font;
    this.charSetName = charSetName;
    charMap = new HashMap<>();

    buildTexture();
  }

  private String getAllAvailableChars(String charSetName) {
    CharsetEncoder ce = Charset.forName(charSetName).newEncoder();
    StringBuilder result = new StringBuilder();
    for (char c = 0; c < Character.MAX_VALUE; c++) {
      if (ce.canEncode(c)) {
        result.append(c);
      }
    }
    return result.toString();
  }

  private void buildTexture() throws IOException {
    // Get the font metrics for each character for the selected font by using image
    BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2D = img.createGraphics();
    g2D.setFont(font);
    FontMetrics fontMetrics = g2D.getFontMetrics();

    String allChars = getAllAvailableChars(charSetName);
    this.width = 0;
    this.height = 0;
    for (char c : allChars.toCharArray()) {
      // Get the size for each character and update global image size
      CharInfo charInfo = new CharInfo(width, fontMetrics.charWidth(c));
      charMap.put(c, charInfo);
      width += charInfo.getWidth();
      height = Math.max(height, fontMetrics.getHeight());
    }
    g2D.dispose();

    // Create the image associated to the charset
    img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    g2D = img.createGraphics();
    g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2D.setFont(font);
    fontMetrics = g2D.getFontMetrics();
    g2D.setColor(Color.WHITE);
    g2D.drawString(allChars, 0, fontMetrics.getAscent());
    g2D.dispose();

    // Dump image to a byte buffer
    InputStream is;
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      ImageIO.write(img, IMAGE_FORMAT, out);
      out.flush();
      is = new ByteArrayInputStream(out.toByteArray());
    }

    texture = new Texture(is);
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public Texture getTexture() {
    return texture;
  }

  public CharInfo getCharInfo(char c) {
    return charMap.get(c);
  }

  public static class CharInfo {

    private final int startX;
    private final int width;

    public CharInfo(int startX, int width) {
      this.startX = startX;
      this.width = width;
    }

    public int getStartX() {
      return startX;
    }

    public int getWidth() {
      return width;
    }
  }
}
