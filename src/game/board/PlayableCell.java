package game.board;

import java.util.Objects;

import game.enums.GameChip;

/**
 * A PlayableCell is any cell on the board that can be played to be a player when their card
 * matches the attempted card from the player. Jacks apply special behaviors
 */
public class PlayableCell implements Cell {

  private final Card card;
  private GameChip chip;

  public PlayableCell(Card card, GameChip chip) {
    this.card = Objects.requireNonNull(card);
    this.chip = chip;
  }

  @Override
  public Card getCard() {
    return this.card;
  }

  @Override
  public boolean hasChip() {
    return !this.chip.equals(GameChip.NONE);
  }

  @Override
  public GameChip getChip() throws IllegalStateException {
    if (!this.hasChip()) {
      throw new IllegalStateException("Cell does not have a chip");
    }
    return this.chip;
  }

  @Override
  public void setChip(GameChip toSet) {
    this.chip = toSet;
  }

  @Override
  public Cell copy() {
    return new PlayableCell(this.card, this.chip);
  }
}
