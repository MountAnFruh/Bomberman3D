package proj.pos.bomberman.game;

import proj.pos.bomberman.engine.GameItem;
import proj.pos.bomberman.engine.graphics.Material;
import proj.pos.bomberman.engine.graphics.Mesh;
import proj.pos.bomberman.engine.graphics.OBJLoader;
import proj.pos.bomberman.engine.graphics.Texture;

import java.io.IOException;

/**
 * @author Robert Schmölzer
 * @since 05.05.2018
 */
public class SkyBox extends GameItem {

  public SkyBox(String objModel, String textureFile) throws IOException {
    super();
    Mesh skyBoxMesh = OBJLoader.loadMesh(objModel);
    Texture skyBoxTexture = new Texture(textureFile);
    skyBoxMesh.setMaterial(new Material(skyBoxTexture, 0.0f));
    setMesh(skyBoxMesh);
    setPosition(0, 0, 0);
  }
}
