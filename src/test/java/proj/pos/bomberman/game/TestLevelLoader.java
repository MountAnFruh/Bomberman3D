package proj.pos.bomberman.game;

import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;

/**
 * @author Andreas Fruhwirt
 * @since 05.05.2018
 */
public class TestLevelLoader {

  private static final String MAP_TEST = "111111111111111111111\n" +
          "111111111111111111111\n" +
          "110000000010000000011\n" +
          "110110111010111011011\n" +
          "110110111010111011011\n" +
          "110003333333333300011\n" +
          "110113131111131311011\n" +
          "110003133313331300011\n" +
          "111113111313111311111\n" +
          "144413133333331314441\n" +
          "111113131121131311111\n" +
          "133333331222133333331\n" +
          "111113131111131311111\n" +
          "144413133333331314441\n" +
          "111113131111131311111\n" +
          "110003333313333300011\n" +
          "110113111313111311011\n" +
          "110013333333333310011\n" +
          "111010101111101010111\n" +
          "110000100010001000011\n" +
          "110111111010111111011\n" +
          "110000000000000000011\n" +
          "111111111111111111111\n" +
          "111111111111111111111\n";

  @Test
  public void testLevelLoadingWithIDs() throws IOException {
    String loadedMap = "", originalMap = "";
    int[][] idsOriginal = LevelLoader.loadIDs("/map_test.png");
    int[][] ids = LevelLoader.loadLayout(0.8f, "/map_test.png");
    for (int y = 0; y < ids.length; y++) {
      for (int x = 0; x < ids[y].length; x++) {
        loadedMap += ids[y][x];
        originalMap += idsOriginal[y][x];
      }
      loadedMap += "\n";
      originalMap += "\n";
    }
    System.out.println(loadedMap);
    assertEquals(MAP_TEST, originalMap);
  }

}
