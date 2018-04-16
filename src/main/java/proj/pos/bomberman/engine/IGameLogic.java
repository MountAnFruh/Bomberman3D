package proj.pos.bomberman.engine;

import proj.pos.bomberman.engine.graphics.Window;

public interface IGameLogic {

  /**
   * Initialisiert Alles für das Spiel
   *
   * @param window Das Window-Objekt
   */
  void init(Window window);

  /**
   * Bearbeitet die Eingabe
   *
   * @param window Das Window-Objekt
   * @param mouseInput Das MouseInput-Objekt
   */
  void input(Window window, MouseInput mouseInput);

  /**
   * Aktualisiert die Spiele-Logik
   *
   * @param delta Millisekunden die zwischen dem letzten Update vergangen sind
   * @param mouseInput Das MouseInput-Objekt
   */
  void update(double delta, MouseInput mouseInput);

  /**
   * Rendert die Spiele-Logik
   *
   * @param window Das Window-Objekt
   */
  void render(Window window);

  /**
   * Räumt Alles für das Spiel auf
   */
  void cleanup();
}
