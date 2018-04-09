package proj.pos.bomberman.engine;

import proj.pos.bomberman.engine.graphics.Window;

public interface IGameLogic {

  /**
   * Initialisiert Alles für das Spiel
   */
  void init();

  /**
   * Bearbeitet die Eingabe
   *
   * @param window Das Window-Objekt
   */
  void input(Window window);

  /**
   * Aktualisiert die Spiele-Logik
   *
   * @param delta Millisekunden die zwischen dem letzten Update vergangen sind
   */
  void update(double delta);

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
