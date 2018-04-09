package proj.pos.bomberman;

import proj.pos.bomberman.engine.IGameLogic;
import proj.pos.bomberman.engine.Window;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class DummyGame implements IGameLogic {

    private int direction = 0;

    private float color = 0.0f;

    private final Renderer renderer;

    public DummyGame() {
        this.renderer = new Renderer();
    }

    @Override
    public void init() {
        renderer.init();
    }

    @Override
    public void input(Window window) {
        if(window.isKeyPressed(GLFW_KEY_UP)) {
            direction = 1;
        } else if(window.isKeyPressed(GLFW_KEY_DOWN)) {
            direction = -1;
        } else {
            direction = 0;
        }
    }

    @Override
    public void update(double delta) {
        color += direction * 0.01f;
        if(color > 1) {
            color = 1.0f;
        } else if(color < 0) {
            color = 0.0f;
        }
    }

    @Override
    public void render(Window window) {
        if(window.isResized()) {
            glViewport(0,0,window.getWidth(),window.getHeight());
            window.setResized(false);
        }

        window.setClearColor(color, color, color, 0.0f);
        renderer.clear();
    }
}
