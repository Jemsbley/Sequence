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

  /**
   * Creates a PlayableCell with no chip and the provided card.
   * @param card the card to associate with this cell
   */
  public PlayableCell(Card card) {
    this.card = Objects.requireNonNull(card);
    this.chip = GameChip.NONE;
  }

  /**
   * Creates a PlayableCell with the provided chip and card.
   * Effectively useful in copying
   * @param card the card to be stored
   * @param chip the chip to be stored
   */
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
