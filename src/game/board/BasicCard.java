package game.board;

import game.enums.CardSuit;
import game.enums.CardValue;

public record BasicCard(CardValue value, CardSuit suit) implements Card {

  @Override
  public boolean sameCard(Card other) {
    return (this == other) ||
            (this.value.equals(other.value()) && this.suit.equals(other.suit()));
  }

}
