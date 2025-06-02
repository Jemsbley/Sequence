package game.model;

import java.util.List;
import java.util.Map;

import game.board.Card;
import game.board.GameBoard;
import game.board.GameHand;
import game.board.GamePosition;
import game.board.SequenceHand;
import game.controller.SequenceController;
import game.enums.GameChip;
import game.enums.SequenceType;
import game.scorekeeper.ScoreKeeper;

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
   * @return whether the game has ended
   */
  boolean isGameOver();

  /**
   * Determines the winner of the game assuming the game is over. Returns GameChip.NONE if there
   * is a tie
   * @return the winning team's GameChip
   */
  GameChip getWinner();

  /**
   * Returns a copy of the current board state of the game.
   * @return a copy of the current board state of the game
   */
  GameBoard getBoard();

  /**
   * Returns a copy of the current hand state for the given player.
   * @param controller the player to check
   * @return an extensive copy of that player's hand
   */
  GameHand getHand(SequenceController controller);

  /**
   * Records an observation of the sequences currently completed in the game.
   * @return a map containing lists of all sequences which begin at each point
   */
  Map<GamePosition, List<SequenceType>> getSequences();

  /**
   * Determines the number of sequences that the given player has.
   * @param player a controller for the game
   * @return how many sequences the player has
   */
  int numSequences(SequenceController player);

  /**
   * Accesses a copy of the current lists of positions where each chip is.
   * @return a map from GameChip to lists of GamePositions which are held by each player
   */
  Map<GameChip, List<GamePosition>> getChips();
}
