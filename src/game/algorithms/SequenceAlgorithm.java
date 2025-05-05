package game.algorithms;

import game.controller.SequenceController;
import game.model.PlayableSequenceModel;

/**
 * A Sequence Algorithm is some object that can receive the model state and their controller,
 * then makes a decision about what is the best move under some rule and broadcasts it to
 * the receiver.
 */
public interface SequenceAlgorithm {

  /**
   * Determines the next best move under this algorithm's rules given the game info.
   * @param model the model to observe
   * @param receiver the controller to broadcast the move to
   */
  void beginTurn(PlayableSequenceModel model, SequenceController receiver);

}
