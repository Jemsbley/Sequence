package game.board;

import game.enums.GameChip;

/**
 * A Cell in a standard game of Sequence is a singular box on the board
 * that is used in making a sequence.
 * When a cell is not playable:
 * The cell contains no card or chip and is a free space (counting in all sequences)
 * When a cell is playable:
 * Players play their chips to the cells when they have cards matching the cell. Jacks
 * have special behaviors.
 * Other cells types are possible in variants of the game
 */
public interface Cell {

  /**
   * Accesses the card on this cell.
   * @return the card on this cell
   */
  Card getCard();

  /**
   * Determines if this cell has a chip. A cell does not have a chip when its
   * chip value is GameChip.NONE
   * @return if this cell holds a chip
   */
  boolean hasChip();

  /**
   * Accesses the chip on this cell.
   * @return the chip on this cell
   * @throws IllegalStateException if this cell currently has no chip
   */
  GameChip getChip() throws IllegalStateException;

  /**
   * Sets the chip on this cell to the provided one.
   * @param toSet the chip to set to this cell's chip value
   */
  void setChip(GameChip toSet);

  /**
   * Creates a copy of this cell. This allows copies of this cell to be made and mutated
   * @return a copy of this cell
   */
  Cell copy();

}
