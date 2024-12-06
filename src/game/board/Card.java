package game.board;

import game.enums.CardSuit;
import game.enums.CardValue;

/**
 * A Card in a game of Sequence has a value and a suit, and represents the physical cards
 * used in the game as well as the images that are held on the cells of the physical board.
 * Playable cards are used to make a move to a cell that matches in card. Jacks have special
 * logic.
 */
public interface Card {

  /**
   * Accesses the value of a given card.
   * @return the value of the card
   */
  CardValue getValue();

  /**
   * Accesses the suit of a given card.
   * @return the suit of the card
   */
  CardSuit getSuit();

  /**
   * Determines if this card is the same card as a given card either extensively or intensively.
   * Cards are the same when they have the same value and suit.
   * @param other the card to compare to
   * @return if the provided card is the same card as this card extensively or intensively
   */
  boolean sameCard(Card other);

}
