package proj.pos.bomberman.game;

import org.joml.Vector3f;
import org.joml.Vector4f;
import proj.pos.bomberman.engine.GameItem;
import proj.pos.bomberman.engine.IHud;
import proj.pos.bomberman.engine.graphics.*;
import proj.pos.bomberman.engine.graphics.Window;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Minimap implements IHud {

    private static final Font FONT = new Font("Consolas", Font.PLAIN, 20);

    private static final String CHARSET = "ISO-8859-1";

    private static final float BLOCKSCALE = 20f;

    private static final float MINIMAPMOVEDX = 10f;
    private static final float MINIMAPMOVEDY = 90f;

    private final Level level;

    private final List<GameItem> gameItems = new ArrayList<>();

    private final TextItem minimapText;
    private final TextItem coordinateText;
    private final GameItem compassItem;

    private final Player player;
    private final Vector3f movedLevel;
    private final float scaleLevel;

  private final Mesh fixBlock;
  private final Mesh destBlock;
  private final Mesh emptyBlock;
  private final Mesh bombBlock;

  private GameItem[][] blockItems;

    public Minimap(Level level, Vector3f movedLevel, float scaleLevel, Player player) throws IOException {
        FontTexture fontTexture = new FontTexture(FONT, CHARSET);
        this.level = level;
        this.movedLevel = movedLevel;
        this.scaleLevel = scaleLevel;
        this.player = player;

        // Create compass
        Mesh mesh = OBJLoader.loadMesh("/models/compass.obj");
        Material material = new Material();
        material.setAmbientColor(new Vector4f(1, 0, 0, 1));
        mesh.setMaterial(material);
        compassItem = new GameItem(mesh);
        compassItem.setScale(BLOCKSCALE);
        // Rotate to transform it to screen coordinates
        compassItem.setRotation(0f, 180f, 180f);
        gameItems.add(compassItem);

        this.minimapText = new TextItem("Minimap: ", fontTexture);
        this.coordinateText = new TextItem("Coordinates: ", fontTexture);

        this.minimapText.getMesh().getMaterial().setAmbientColor(new Vector4f(1, 1, 1, 1));
        this.coordinateText.getMesh().getMaterial().setAmbientColor(new Vector4f(1, 1, 1, 1));
        gameItems.add(minimapText);
        gameItems.add(coordinateText);

      fixBlock = OBJLoader.loadMesh("/models/rectangle.obj");
        Texture texture = new Texture("/textures/stone_small.png");
        material = new Material(texture, 0.0f);
        fixBlock.setMaterial(material);

      destBlock = OBJLoader.loadMesh("/models/rectangle.obj");
        texture = new Texture("/textures/brick_small.png");
        material = new Material(texture, 0.0f);
        destBlock.setMaterial(material);

      bombBlock = OBJLoader.loadMesh("/models/rectangle.obj");
      material = new Material();
      material.setAmbientColor(new Vector4f(1, 0, 0, 1));
      bombBlock.setMaterial(material);

      emptyBlock = OBJLoader.loadMesh("/models/rectangle.obj");
        material = new Material();
        material.setAmbientColor(new Vector4f(1, 1, 1, 1));
        emptyBlock.setMaterial(material);

      doDrawing();
    }

  public void doDrawing() {
    gameItems.clear();
    // Create blocks
    int[][] layout = level.getLayout();
    blockItems = new GameItem[layout.length][layout[0].length];
    for (int y = 0; y < blockItems.length; y++) {
      for (int x = 0; x < blockItems[y].length; x++) {
        GameItem gameItem;
        if (layout[y][x] == 1) {
          gameItem = new GameItem(fixBlock);
        } else if (layout[y][x] == 4) {
          gameItem = new GameItem(destBlock);
        } else if (layout[y][x] == 5) {
          gameItem = new GameItem(bombBlock);
        } else {
          gameItem = new GameItem(emptyBlock);
        }
        gameItem.setScale(BLOCKSCALE);
        gameItem.setRotation(0f, 180f, 180f);
        blockItems[x][y] = gameItem;
        gameItems.add(gameItem);
      }
    }
  }

    @Override
    public GameItem[] getGameItems() {
        return gameItems.toArray(new GameItem[0]);
    }

    public void update(Window window) {
        this.minimapText.setPosition(10f, 10f, 0);
        this.coordinateText.setPosition(10f, 30f, 0);
        this.coordinateText.setText("Coordinates: " + player.getPosition().toString());
        for(int i = 0;i < blockItems.length;i++) {
            for (int j = 0; j < blockItems[i].length; j++) {
                if(blockItems[i][j] != null) {
                  blockItems[i][j].setPosition(MINIMAPMOVEDX + i * BLOCKSCALE, MINIMAPMOVEDY + j * BLOCKSCALE, 0.99f);
                }
            }
        }
      if (level.insideXZ(player)) {
            if(!gameItems.contains(compassItem)) gameItems.add(compassItem);
            this.compassItem.setPosition(MINIMAPMOVEDX + (player.getPosition().x - movedLevel.x * scaleLevel) * BLOCKSCALE,
                    MINIMAPMOVEDY - BLOCKSCALE + (player.getPosition().z - movedLevel.z * scaleLevel) * BLOCKSCALE, 0.999f);
        } else {
            if(gameItems.contains(compassItem)) gameItems.remove(compassItem);
        }
    }

    public void rotateCompass(float angle) {
        this.compassItem.setRotation(0f, 180f, 180f + angle);
    }
}
