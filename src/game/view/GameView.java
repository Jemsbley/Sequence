package game.view;

/**
 * A GameView is some implementation of a visual library that follows standardized viewing behavior
 * for viewing the entire game state for one specific player.
 */
public interface GameView {

  /**
   * Redraws this view and all its subcomponents.
   */
  void redraw();

  /**
   * Shows a popup window with the given message.
   * @param message the message to show
   */
  void popUp(String message);

  /**
   * Prevents any more inputs from being received once the game has been concluded.
   */
  void gameOver();
}
