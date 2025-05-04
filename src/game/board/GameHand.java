package game.board;

import game.enums.GameChip;

/**
 * Represents a hand in the game of sequence. This is effectively just a list of cards
 * associated with a team/player
 */
public interface GameHand {

  /**
   * Accesses the card at the given index in the hand if possible.
   * @param index the index to check
   * @return the card at that index
   */
  Card getCardAt(int index);

  /**
   * Removes the card at the given index in the hand if possible.
   * @param index the index to receive from
   * @return the card at that index
   */
  Card removeCardAt(int index);

  /**
   * Adds a given card to the hand.
   * @param toAdd the card to add
   */
  void addCard(Card toAdd);

  /**
   * Determines the size of this hand, or the number of cards it has.
   * @return the card count
   */
  int size();

  /**
   * Gets the team associated with this hand. Note that this does not specify which player
   * is associated with it.
   * @return the GameChip for this hand
   */
  GameChip getTeam();

  /**
   * Creates and returns a copy of this hand.
   * @return an extensive copy of this hand
   */
  GameHand copy();

}
