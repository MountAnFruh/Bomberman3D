package proj.pos.bomberman;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertArrayEquals;

public class TestUtils {

  private String txtfileContent = "testfile~130.\r\nty";

  @Test
  public void testLoadResourceReturnsContent() throws Exception {
    String content = Utils.loadResource("/txtfile.txt");
    assertEquals(txtfileContent, content);
  }

  @Test(expected = FileNotFoundException.class)
  public void testLoadResourceThrowsFileNotFoundExceptionOnContent() throws Exception {
    String content = Utils.loadResource("/test");
  }

  @Test
  public void testLoadResourceReturnsContentLines() throws Exception {
    String[] lines = Utils.readAllLines("/txtfile.txt").toArray(new String[0]);
    String[] expect = txtfileContent.split("\r\n");
    assertArrayEquals(expect, lines);
  }

  @Test(expected = FileNotFoundException.class)
  public void testLoadResourceThrowsFileNotFoundExceptionOnLines() throws Exception {
    List<String> lines = Utils.readAllLines("/test");
  }
}
