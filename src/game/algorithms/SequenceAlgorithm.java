package game.algorithms;

import game.controller.SequenceController;
import game.model.PlayableSequenceModel;

public interface SequenceAlgorithm {

  void beginTurn(PlayableSequenceModel model, SequenceController receiver);

}
