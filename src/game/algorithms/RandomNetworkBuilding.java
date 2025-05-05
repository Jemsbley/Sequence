package game.algorithms;

import java.util.Random;

import game.controller.SequenceController;
import game.model.PlayableSequenceModel;

/**
 * Randomly alternates between doing an offensive or defensive move using the
 * Defensive and Offensive network builders.
 */
public class RandomNetworkBuilding implements SequenceAlgorithm {
  @Override
  public void beginTurn(PlayableSequenceModel model, SequenceController receiver) {
    SequenceAlgorithm def = new DefensiveNetworkBuilding();
    SequenceAlgorithm off = new OffensiveNetworkBuilding();
    Random r = new Random();
    if (r.nextBoolean()) {
      def.beginTurn(model, receiver);
    } else {
      off.beginTurn(model, receiver);
    }
  }
}
