package game.board;

import game.enums.CardSuit;
import game.enums.CardValue;

/**
 * A representation of a card in the game of sequence. Has a
 * @param value the value of the card
 * @param suit the suit of the card
 */
public record BasicCard(CardValue value, CardSuit suit) implements Card {

  @Override
  public boolean sameCard(Card other) {
    return (this == other) ||
            (this.value.equals(other.value()) && this.suit.equals(other.suit()));
  }

}
