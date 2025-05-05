package game.enums;

import java.awt.*;

/**
 * Each of the four standard card suits.
 */
public enum CardSuit {

  CLUBS, SPADES, DIAMONDS, HEARTS;

  public Color color() {
    if (this.equals(CardSuit.CLUBS) || this.equals(CardSuit.SPADES)) {
      return new Color(30, 30, 30);
    } else {
      return new Color(150, 30, 30);
    }
  }

}
