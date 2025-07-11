package game.model;

import java.util.List;
import java.util.Random;

import game.board.GameBoard;
import game.controller.GameMove;
import game.controller.SequenceController;
import game.scorekeeper.ScoreKeeper;
import game.view.GameView;

/**
 * The behaviors of a mutable sequence model. This includes all functionality that would alter
 * the game state. This is an extension of the read only model in order to allow access to other
 * read only methods.
 */
public interface PlayableSequenceModel extends ReadOnlySequenceModel {

  /**
   * Plays the provided game move assuming it is valid under the current player's turn and
   * requests a move from the next player in the cycle. Will follow proper control flow when
   * the game ends
   * @param move the provided move to play
   */
  void playToCell(GameMove move);

  /**
   * Initializes the game state with the provided information. Creates a cyclic turn order,
   * initializes hands, sequence counts, and card counts
   * @param gameBoard the board to be played to
   * @param players the list of controllers in proper turn order
   * @param shuffler the random object to shuffle the deck
   */
  void initializeGame(GameBoard gameBoard, List<SequenceController> players, Random shuffler);

  /**
   * Begins gameplay by requesting a move from the first player
   */
  void beginPlaying();

  /**
   * Resets the current deck by reshuffling and ensuring that none of the cards currently in the
   * players' hands are in the new deck (Effectively the same as reshuffling the discard pile).
   */
  void resetDeck();

  /**
   * Adds a view to the model system so it can be repainted after each move.
   * @param toAdd the model to add
   */
  void addView(GameView toAdd);

  /**
   * Allows a player to discard and replace a card if there are no playable spaces for said card.
   * @param cardIdx the card to attempt to discard
   */
  void deadCard(int cardIdx);


  /**
   * Adds the given Scorekeeper to log the results of this game.
   * @param sk the scorekeeper to use
   */
  void addScoreKeeper(ScoreKeeper sk);

}
