package proj.pos.bomberman.game;

import de.matthiasmann.twl.utils.PNGDecoder;
import org.joml.Vector2i;

import java.awt.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LevelLoader {

  /* map-id
   * 0 | WEISS   | Leer
   * 1 | SCHWARZ | Konstanter Block
   * 2 | GRUEN   | Spawnpoint
   * 3 | BLAU    | Block per Zufall
   * 4 | ROT     | Zerstörbarer Block
   */
  private static Color[] colors = new Color[]{
          Color.WHITE, Color.BLACK, Color.GREEN, Color.BLUE, Color.RED
  };

  private static Random rand = new Random();

  /**
   * Lädt die IDs für ein bestimmtes Level:
   * id | Farbe   | Beschreibung
   * 0  | WEISS   | Leer
   * 1  | SCHWARZ | Konstanter Block
   * 2  | GRUEN   | Spawnpoint
   * 3  | BLAU    | Block per Zufall
   * 4  | ROT     | Zerstörbarer Block
   *
   * @param fileName Der Pfad zum Level-Bild
   * @return Die IDs
   */
  public static int[][] loadIDs(String fileName) throws IOException {
    PNGDecoder decoder = new PNGDecoder(LevelLoader.class.getResourceAsStream(fileName));
    ByteBuffer buf = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
    decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
    buf.flip();
    byte[] bytes = new byte[buf.capacity()];
    int[][] ids = new int[decoder.getHeight() + 2][decoder.getWidth() + 2];
    for (int i = 0; i < ids.length; i++) {
      for (int j = 0; j < ids[i].length; j++) {
        ids[i][j] = 1;
      }
    }
    buf.get(bytes, 0, buf.capacity());
    for (int i = 0; i < bytes.length; i += 4) {
      int x = ((i / 4) % decoder.getWidth()) + 1;
      int y = ((i / 4) / decoder.getWidth()) + 1;
      ids[y][x] = -1;
      int r = bytes[i] * (-255);
      int g = bytes[i + 1] * (-255);
      int b = bytes[i + 2] * (-255);
      int a = bytes[i + 3] * (-255);
      Color color = new Color(r, g, b, a);
      for (int id = 0; id < colors.length; id++) {
        Color idColor = colors[id];
        if (color.equals(idColor)) {
          ids[y][x] = id;
        }
      }
    }
    return ids;
  }

  /**
   * Lädt das Level-Objekt für ein bestimmtes Level
   *
   * @param remPercent Prozentanzahl wie viele Blöcke von den zufälligen gelöscht werden soll
   * @param fileName   Der Pfad zum Level-Bild
   * @return Das Layout vom Level
   */
  public static int[][] loadLayout(float remPercent, String fileName) throws IOException {
    int[][] ids = loadIDs(fileName);
    List<Vector2i> rCoordinates = new ArrayList<>();
    for (int y = 0; y < ids.length; y++) {
      for (int x = 0; x < ids[y].length; x++) {
        if (ids[y][x] == 3) {
          rCoordinates.add(new Vector2i(x, y));
          ids[y][x] = 0;
        }
      }
    }
    int blockRemoved = (int) ((float) rCoordinates.size() * remPercent);
    for (int i = 0; i < blockRemoved; i++) {
      int OG = rCoordinates.size() - 1;
      int UG = 0;
      rCoordinates.remove(rand.nextInt(OG - UG + 1) + UG);
    }
    for (Vector2i coordinate : rCoordinates) {
      ids[coordinate.y][coordinate.x] = 4;
    }
    return ids;
  }

}
