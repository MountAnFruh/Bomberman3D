package proj.pos.bomberman;

import org.junit.Test;

import java.io.FileNotFoundException;

import static junit.framework.TestCase.assertEquals;

public class TestUtils {

  private String txtfileContent = "testfile~130.\r\nty";

  @Test
  public void loadResourceReturnsContent() throws Exception {
    String content = Utils.loadResource("/txtfile.txt");
    assertEquals(txtfileContent, content);
  }

  @Test(expected = FileNotFoundException.class)
  public void loadResourceThrowsFileNotFoundException() throws Exception {
    String content = Utils.loadResource("/test");
  }

}
