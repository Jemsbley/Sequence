package game.board;

import game.enums.GameChip;

/**
 * A FreeSpaceCell is a cell on the board (in the corners on a standard board) that is never
 * played to, but counts as a chip in a sequence for all teams
 */
public class FreeSpaceCell implements Cell {
  @Override
  public Card getCard() {
    throw new IllegalStateException("Free spaces have no card");
  }

  @Override
  public boolean hasChip() {
    return true;
  }

  @Override
  public GameChip getChip() throws IllegalStateException {
    return GameChip.ALL;
  }

  @Override
  public void setChip(GameChip toSet) {
    throw new IllegalStateException("Cannot set the chip of a free space");
  }

  @Override
  public Cell copy() {
    return this;
  }
}
