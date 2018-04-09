package proj.pos.bomberman;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class Utils {

  /**
   * Holt sich die Resource auf dem Pfad {@code filePath} und gibt ihren Inhalt zurück
   *
   * @param filePath Der Dateipfad
   * @return Den Inhalt der Resource
   * @throws IOException
   */
  public static String loadResource(String filePath) throws IOException {
    String result = null;
    try (InputStream in = Class.forName(Utils.class.getName()).getResourceAsStream(filePath)) {
      Scanner scanner = new Scanner(in, "UTF-8");
      result = scanner.useDelimiter("\\A").next();
    } catch (ClassNotFoundException ex) {
      ex.printStackTrace();
    }
    return result;
  }
}
