import java.util.List;
import java.util.Random;

import game.algorithms.AsRandomAsCanBe;
import game.algorithms.DefensiveNetworkBuilding;
import game.algorithms.FarsightedDefensiveNetworkBuilding;
import game.algorithms.FarsightedOffensiveNetworkBuilding;
import game.algorithms.FarsightedOffensiveSavingNetworkBuilding;
import game.algorithms.FarsightedScoredNetworkBuilding;
import game.algorithms.InverseNetworkBuilding;
import game.algorithms.OffensiveNetworkBuilding;
import game.algorithms.RandomNetworkBuilding;
import game.algorithms.ScoredNetworkBuilding;
import game.algorithms.TwoNeurons;
import game.board.GameBoard;
import game.board.StandardBoardType;
import game.controller.AlgorithmController;
import game.controller.HumanController;
import game.controller.SequenceController;
import game.enums.GameChip;
import game.model.PlayableSequenceModel;
import game.model.SequenceModel;
import game.scorekeeper.ScoreKeeper;
import game.scorekeeper.TwoPlayerScoreKeeper;
import game.view.GameFrame;

public class MainExperiments {

  public static void main(String[] args) {

    PlayableSequenceModel model = new SequenceModel();
    GameBoard def = new StandardBoardType();

    int realplayers = 0;

    if (realplayers == 0) {
      SequenceController blueCPU = new AlgorithmController(new AsRandomAsCanBe(),
              model, GameChip.BLUE);
      SequenceController redCPU = new AlgorithmController(new AsRandomAsCanBe(),
              model, GameChip.RED);
      model.initializeGame(def, List.of(redCPU, blueCPU), new Random(0));
      GameFrame gf3 = new GameFrame(model, blueCPU);
      GameFrame gf4 = new GameFrame(model, redCPU);
      blueCPU.addView(gf3);
      redCPU.addView(gf4);
      model.addView(gf3);
      model.addView(gf4);
      gf3.setVisible(true);
      gf4.setVisible(true);

      ScoreKeeper sk = new TwoPlayerScoreKeeper();
      sk.addPlayer(GameChip.BLUE);
      sk.addPlayer(GameChip.RED);
      for (int reps = 0; reps < 1000; reps += 1) {
        model = new SequenceModel();
        def = new StandardBoardType();
        blueCPU = new AlgorithmController(new FarsightedScoredNetworkBuilding(),
                model, GameChip.BLUE);
        redCPU = new AlgorithmController(new AsRandomAsCanBe(),
                model, GameChip.RED);
        model.initializeGame(def, List.of(redCPU, blueCPU), new Random());
        blueCPU.addView(gf3);
        redCPU.addView(gf4);
        model.addView(gf3);
        model.addView(gf4);
        gf3.reset(model, blueCPU);
        gf4.reset(model, redCPU);
        model.addScoreKeeper(sk);
        model.beginPlaying();
        if (model.numSequences(blueCPU) >= 2 && model.numSequences(redCPU) >= 2) {
          throw new IllegalStateException("Both won but somehow nobody did");
        }
      }
      System.out.println(sk.displayResults());
    }
    else if (realplayers == 1) {
      SequenceController blueGuy = new HumanController(model, GameChip.BLUE);
      SequenceController redCPU = new AlgorithmController(new FarsightedOffensiveSavingNetworkBuilding(),
              model, GameChip.RED);
      model.initializeGame(def, List.of(blueGuy, redCPU), new Random());
      GameFrame gf = new GameFrame(model, blueGuy);
      GameFrame gf4 = new GameFrame(model, redCPU);
      blueGuy.addView(gf);
      redCPU.addView(gf4);
      model.addView(gf);
      model.addView(gf4);
      gf.setVisible(true);
      gf4.setVisible(true);
      model.beginPlaying();
    } else if (realplayers == 2) {
      SequenceController blueGuy = new HumanController(model, GameChip.BLUE);
      SequenceController redGuy = new HumanController(model, GameChip.RED);
      model.initializeGame(def, List.of(redGuy, blueGuy), new Random(0));
      GameFrame gf = new GameFrame(model, blueGuy);
      GameFrame gf2 = new GameFrame(model, redGuy);
      blueGuy.addView(gf);
      redGuy.addView(gf2);
      model.addView(gf);
      model.addView(gf2);
      gf.setVisible(true);
      gf2.setVisible(true);
      model.beginPlaying();
    }


   }
}
