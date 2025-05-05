package game.board;

import java.util.List;
import java.util.Map;

import game.enums.GameChip;
import game.enums.SequenceType;

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
   * @throws IllegalArgumentException if the location is invalid or is locked
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

  /**
   * Determines if the given location is a valid position on the board.
   * @param location the location to check
   * @return if the position is on the board
   */
  boolean isValidLocation(GamePosition location);

  /**
   * Creates and returns an extensive copy of the state of this board.
   * @return the generated copy
   */
  GameBoard copy();

  /**
   * Returns a map that provides all locations of all cells on the board. This is extremely useful
   * in automating gameplay and minimizing the amount of observations that the automation needs to
   * make
   * @return the cardLocations for this board type
   */
  Map<Card, List<GamePosition>> cardLocations();

  /**
   * Locks the board at the given position in a given direction.
   * @param mode the type of sequence direction to lock it in
   * @param location the position to lock
   */
  void lock(GamePosition location, SequenceType mode);

  /**
   * Determines if the given location is currently locked in any direction.
   * @param location the location to check
   * @return if that location is locked
   */
  boolean isLocked(GamePosition location);

  /**
   * Determines if the given location is currently locked in the given mode.
   * @param location the location to check
   * @param mode the sequence type to check
   * @return if that location is locked
   */
  boolean isLocked(GamePosition location, SequenceType mode);

  /**
   * Determines the lock states for all sequence types at the given location.
   * @param location the location to check
   * @return a map of the lock states
   */
  Map<SequenceType, Boolean> getLocks(GamePosition location);

  /**
   * Determines if there are any chips on the board.
   * @return whether a single space contains a player chip
   */
  boolean isEmpty();

}
