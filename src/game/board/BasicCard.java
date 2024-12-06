package game.board;

import game.enums.CardSuit;
import game.enums.CardValue;

public class BasicCard implements Card {

  private final CardValue value;
  private final CardSuit suit;

  public BasicCard(CardValue value, CardSuit suit) {
    this.value = value;
    this.suit = suit;
  }


  @Override
  public CardValue getValue() {
    return this.value;
  }

  @Override
  public CardSuit getSuit() {
    return this.suit;
  }

  @Override
  public boolean sameCard(Card other) {
    return (this == other) ||
            (this.value.equals(other.getValue()) && this.suit.equals(other.getSuit()));
  }

}
