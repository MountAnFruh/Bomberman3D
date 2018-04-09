package proj.pos.bomberman.engine;

public interface IGameLogic {

    void init();

    void input(Window window);

    void update(double delta);

    void render(Window window);
}
