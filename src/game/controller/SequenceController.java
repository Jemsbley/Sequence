package game.controller;

import game.enums.GameChip;
import game.model.PlayableSequenceModel;

/**
 * The behaviors of a controller for the game. Controllers respond to requests from the model
 * to play the game, then appropriately operate to generate a move. A controller will often
 * ask some sort of player (human, algorithmic, or heuristic) for a move and play that when
 * it is broadcast.
 */
public interface SequenceController {

  /**
   * Begins playing this controller's turn on the given model. This is where the controller
   * may request moves from the player
   * @param model the model to play to
   */
  void beginTurn(PlayableSequenceModel model);

  /**
   * Receives a move from some external information broadcast in order to make a move.
   * @param moveToPlay the move to be played/attemped
   */
  void receiveMove(GameMove moveToPlay);

  /**
   * Gets the team of this player.
   * @return the GameChip representing this controller's team
   */
  GameChip getTeam();

}
