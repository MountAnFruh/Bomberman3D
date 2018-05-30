package proj.pos.bomberman.game;

import proj.pos.bomberman.engine.GameItem;
import proj.pos.bomberman.engine.graphics.FontTexture;
import proj.pos.bomberman.engine.graphics.Material;
import proj.pos.bomberman.engine.graphics.Mesh;
import proj.pos.bomberman.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TextItem extends GameItem {

  private static final float ZPOS = 0.0f;

  private static final int VERTICES_PER_QUAD = 4;

  private String text;

  private final FontTexture fontTexture;

  public TextItem(String text, FontTexture fontTexture) throws IOException {
    super();
    this.text = text;
    this.fontTexture = fontTexture;
    this.setMesh(buildMesh());
  }

  private Mesh buildMesh() {
    char[] chars = text.toCharArray();
    int numChars = chars.length;

    List<Float> positions = new ArrayList<>();
    List<Float> textCoords = new ArrayList<>();
    float[] normals = new float[0];
    List<Integer> indices = new ArrayList<>();

    float startX = 0;
    for (int i = 0; i < numChars; i++) {
      FontTexture.CharInfo charInfo = fontTexture.getCharInfo(chars[i]);

      // Build a character tile composed by two triangles

      // Left top vertex
      positions.add(startX); // x
      positions.add(0.0f); // y
      positions.add(ZPOS); // z
      textCoords.add((float) charInfo.getStartX() / (float) fontTexture.getWidth());
      textCoords.add(0.0f);
      indices.add(i * VERTICES_PER_QUAD);

      // Left Bottom vertex
      positions.add(startX); // x
      positions.add((float) fontTexture.getHeight()); // y
      positions.add(ZPOS); // z
      textCoords.add((float) charInfo.getStartX() / (float) fontTexture.getWidth());
      textCoords.add(1.0f);
      indices.add(i * VERTICES_PER_QUAD + 1);

      // Right bottom vertex
      positions.add(startX + charInfo.getWidth()); // x
      positions.add((float) fontTexture.getHeight()); // y
      positions.add(ZPOS); // z
      textCoords.add((float) (charInfo.getStartX() + charInfo.getWidth()) / (float) fontTexture.getWidth());
      textCoords.add(1.0f);
      indices.add(i * VERTICES_PER_QUAD + 2);

      // Right top vertex
      positions.add(startX + charInfo.getWidth()); // x
      positions.add(0.0f); // y
      positions.add(ZPOS); // z
      textCoords.add((float) (charInfo.getStartX() + charInfo.getWidth()) / (float) fontTexture.getWidth());
      textCoords.add(0.0f);
      indices.add(i * VERTICES_PER_QUAD + 3);

      // Add indices for left top and bottom right vertices
      indices.add(i * VERTICES_PER_QUAD);
      indices.add(i * VERTICES_PER_QUAD + 2);

      startX += charInfo.getWidth();
    }

    float[] posArr = Utils.floatListToArray(positions);
    float[] textCoordsArr = Utils.floatListToArray(textCoords);
    int[] indicesArr = indices.stream().mapToInt(i -> i).toArray();
    Mesh mesh = new Mesh(posArr, textCoordsArr, normals, indicesArr);
    mesh.setMaterial(new Material(fontTexture.getTexture()));
    return mesh;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
    this.getMesh().deleteBuffers();
    this.setMesh(buildMesh());
  }


}
