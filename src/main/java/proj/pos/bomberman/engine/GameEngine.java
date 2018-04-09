package proj.pos.bomberman.engine;

public class GameEngine implements Runnable {

    public static final int TARGET_FPS = 60;
    public static final int TARGET_UPS = 30;

    private final Window window;
    private final Thread gameLoopThread;
    private final IGameLogic gameLogic;

    private boolean running;

    public GameEngine(String windowTitle, int width, int height,
                      IGameLogic gameLogic) {
        this.gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
        this.window = new Window(windowTitle, width, height);
        this.gameLogic = gameLogic;
        this.running = true;
    }

    public void start() {
        String osName = System.getProperty("os.name");
        if(osName.contains("Mac")) {
            gameLoopThread.run();
        } else {
            gameLoopThread.start();
        }
    }

    @Override
    public void run() {
        try {
            init();
            gameLoop();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void gameLoop() {
        long initTime = System.nanoTime();

        final double TIME_BETWEEN_UPDATES = 1_000_000_000 / TARGET_UPS;
        final double TIME_BETWEEN_RENDERS = 1_000_000_000 / TARGET_FPS;

        double deltaU = 0, deltaF = 0;
        int fps = 0, ups = 0;

        long timer = System.currentTimeMillis();

        while(running && !window.windowShouldClose()) {
            long now = System.nanoTime();
            deltaU += (now - initTime) / TIME_BETWEEN_UPDATES;
            deltaF += (now - initTime) / TIME_BETWEEN_RENDERS;
            initTime = now;

            if(deltaU >= 1) {
                input();
                update(deltaU);
                ups++;
                deltaU--;
            }

            if(deltaF >= 1) {
                render();
                fps++;
                deltaF--;
            }

            if(System.currentTimeMillis() - timer > 1_000) {
                System.out.println(String.format("UPS: %s, FPS: %s",fps,ups));
                fps = 0;
                ups = 0;
                timer += 1_000;
            }
        }
    }

    protected void init() {
        window.init();
        gameLogic.init();
    }

    protected void input() {
        gameLogic.input(window);
    }

    protected void update(double delta) {
        gameLogic.update(delta);
    }

    protected void render() {
        gameLogic.render(window);
        window.update();
    }
}
