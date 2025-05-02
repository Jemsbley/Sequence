package game.board;

import game.enums.GameChip;

/**
 * Behaviors of a board in the game of sequence. A board has a rectangular grid of cells
 * that represent cards that can be played to
 */
public interface GameBoard {

  /**
   * Gets a copy of the cell at the provided location.
   * @param location the 0-based location on the board (see GamePosition)
   * @return a copy of the cell at the provided location
   * @throws IllegalArgumentException if the location is invalid
   */
  Cell getCell(GamePosition location) throws IllegalArgumentException;

  /**
   * Sets the chip at the requested location to the requested chip if possible.
   * Note that boards do not care about game logic and will allow all chip setting
   * (even when cells are already "taken" by players)
   * @param location the location to set
   * @param toSet the chip to set it to
   * @throws IllegalArgumentException if the location is invalid
   */
  void setChip(GamePosition location, GameChip toSet) throws IllegalArgumentException;

  /**
   * Creates a copy of the current board and returns it.
   * @return a copy of the stored board
   */
  Cell[][] getBoard();

  /**
   * Determines if all cells contain chips.
   * @return false if any cell is open, false otherwise
   */
  boolean isFull();

}
