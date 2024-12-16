package game.model;

import game.board.Card;
import game.controller.SequenceController;
import game.enums.GameChip;

/**
 * The behaviors of an immutable model of a sequence game. This includes all functionality that
 * will make observations about the game state, but not change it.
 */
public interface ReadOnlySequenceModel {

  /**
   * Determines the controller who is responsible for the next move of the game.
   * @return the current controller
   */
  SequenceController getCurrentTurn();

  /**
   * Determines the number of one eyed jacks that have yet to be played before a reshuffle.
   * @return the count
   */
  int numOneEyedJacksRemaining();

  /**
   * Determines the number of two eyed jacks that have yet to be played before a reshuffle.
   * @return the count
   */
  int numTwoEyedJacksRemaining();

  /**
   * Determines the number of a given card that have yet to be played before a reshuffle.
   * @param toCheck the card to check for the count of
   * @return the count
   */
  int numXCardRemaining(Card toCheck);

  /**
   * Determines if the game is over. The game is over when one team has two sequences or the game
   * board is full
   * @return
   */
  boolean isGameOver();

  /**
   * Determines the winner of the game assuming the game is over. Returns GameChip.NONE if there
   * is a tie
   * @return the winning team's GameChip
   */
  GameChip getWinner();

}
