package proj.pos.bomberman;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Utils {

  /**
   * Holt sich die Resource auf dem Pfad {@code filePath} und gibt ihren Inhalt zurück
   *
   * @param filePath Der Dateipfad
   * @return Den Inhalt der Resource
   * @throws IOException
   * @throws FileNotFoundException
   */
  public static String loadResource(String filePath) throws IOException, FileNotFoundException {
    String result = null;
    try (InputStream in = Class.forName(Utils.class.getName()).getResourceAsStream(filePath)) {
      if (in == null) throw new FileNotFoundException();
      Scanner scanner = new Scanner(in, "UTF-8");
      result = scanner.useDelimiter("\\A").next();
    } catch (ClassNotFoundException ex) {
      ex.printStackTrace();
    }
    return result;
  }

  /**
   * Holt sich die Resource auf dem Pfad {@code filePath} und gibt ihre Zeilen zurück
   *
   * @param filePath Der Dateipfad
   * @return Die Zeilen der Resource
   * @throws IOException
   * @throws FileNotFoundException
   */
  public static List<String> readAllLines(String filePath) throws IOException, FileNotFoundException {
    List<String> list = new ArrayList<>();
    try (InputStream in = Class.forName(Utils.class.getName()).getResourceAsStream(filePath)) {
      if (in == null) throw new FileNotFoundException();
      try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
        String line;
        while ((line = br.readLine()) != null) {
          list.add(line);
        }
      }
    } catch (ClassNotFoundException ex) {
      ex.printStackTrace();
    }
    return list;
  }
}
