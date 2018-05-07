package proj.pos.bomberman.game;

import org.joml.Vector4f;
import proj.pos.bomberman.engine.GameItem;
import proj.pos.bomberman.engine.IHud;
import proj.pos.bomberman.engine.graphics.*;
import proj.pos.bomberman.engine.graphics.Window;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Minimap implements IHud {

    private static final Font FONT = new Font("Consolas", Font.PLAIN, 20);

    private static final String CHARSET = "ISO-8859-1";

    private static final float BLOCKSCALE = 20f;

    private final Level level;

    private final GameItem[][] blockItems;

    private final List<GameItem> gameItems = new ArrayList<>();

   // private final GameItem[] hudItems;

    private final TextItem minimapText;

    public Minimap(Level level/*, Mesh fixBlock, Mesh destBlock*/) throws IOException {
        FontTexture fontTexture = new FontTexture(FONT, CHARSET);
        this.level = level;

        this.minimapText = new TextItem("Minimap: ", fontTexture);

        this.minimapText.getMesh().getMaterial().setAmbientColor(new Vector4f(1, 1, 1, 1));
        gameItems.add(minimapText);

        Mesh fixBlock = OBJLoader.loadMesh("/models/rectangle.obj");
        Texture texture = new Texture("/textures/stone_small.png");
        Material material = new Material(texture, 0.0f);
        fixBlock.setMaterial(material);

        Mesh destBlock = OBJLoader.loadMesh("/models/rectangle.obj");
        texture = new Texture("/textures/brick_small.png");
        material = new Material(texture, 0.0f);
        destBlock.setMaterial(material);

        Mesh emptyBlock = OBJLoader.loadMesh("/models/rectangle.obj");
        material = new Material();
        material.setAmbientColor(new Vector4f(1, 1, 1, 1));
        emptyBlock.setMaterial(material);

        // Create blocks
        int[][] layout = level.getLayout();
        blockItems = new GameItem[layout.length][layout[0].length];
        for(int y = 0;y < blockItems.length;y++) {
            for(int x = 0;x < blockItems[y].length;x++) {
                GameItem gameItem;
                if (layout[y][x] == 1) {
                    gameItem = new GameItem(fixBlock);
                } else if (layout[y][x] == 4) {
                    gameItem = new GameItem(destBlock);
                } else {
                    gameItem = new GameItem(emptyBlock);
                }
                gameItem.setScale(BLOCKSCALE);
                gameItem.setRotation(0f, 180f, 180f);
                blockItems[y][blockItems[y].length - 1 - x] = gameItem;
                gameItems.add(gameItem);
            }
        }
    }

    @Override
    public GameItem[] getGameItems() {
        return gameItems.toArray(new GameItem[0]);
    }

    public void updateSize(Window window) {
        this.minimapText.setPosition(10f, 10f, 0);
        for(int i = 0;i < blockItems.length;i++) {
            for (int j = 0; j < blockItems[i].length; j++) {
                if(blockItems[i][j] != null) {
                    blockItems[i][j].setPosition(10f + i * BLOCKSCALE, 60f + j * BLOCKSCALE, 0);
                }
            }
        }
        //this.compassItem.setPosition(window.getWidth() - 40f, 50f, 0);
    }
}
