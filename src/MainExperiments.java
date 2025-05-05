import java.util.List;
import java.util.Random;

import game.algorithms.AsRandomAsCanBe;
import game.algorithms.NetworkBuildingMinimizeOneEyes;
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
    SequenceController blueGuy = new HumanController(model, GameChip.BLUE);
    SequenceController redGuy = new HumanController(model, GameChip.RED);
    SequenceController greenCPU = new AlgorithmController(new NetworkBuildingMinimizeOneEyes(),
            model, GameChip.GREEN);
    SequenceController redCPU = new AlgorithmController(new AsRandomAsCanBe(),
            model, GameChip.RED);


    model.initializeGame(def, List.of(redCPU, greenCPU), new Random(0));
    //GameFrame gf = new GameFrame(model, blueGuy);
    //GameFrame gf2 = new GameFrame(model, redGuy);
    GameFrame gf3 = new GameFrame(model, greenCPU);
    GameFrame gf4 = new GameFrame(model, redCPU);
    //blueGuy.addView(gf);
    //redGuy.addView(gf2);
    greenCPU.addView(gf3);
    redCPU.addView(gf4);
    //model.addView(gf);
    //model.addView(gf2);
    model.addView(gf3);
    model.addView(gf4);
    //gf.setVisible(true);
    //gf2.setVisible(true);
    gf3.setVisible(true);
    gf4.setVisible(true);

    model.beginPlaying();

    ScoreKeeper sk = new TwoPlayerScoreKeeper();
    sk.addPlayer(GameChip.GREEN);
    sk.addPlayer(GameChip.RED);
    for (int reps = 0; reps < 1000; reps += 1) {
      model = new SequenceModel();
      def = new StandardBoardType();
      greenCPU = new AlgorithmController(new NetworkBuildingMinimizeOneEyes(),
              model, GameChip.GREEN);
      redCPU = new AlgorithmController(new AsRandomAsCanBe(),
              model, GameChip.RED);
      model.initializeGame(def, List.of(redCPU, greenCPU), new Random(reps));
      greenCPU.addView(gf3);
      redCPU.addView(gf4);
      model.addView(gf3);
      model.addView(gf4);
      gf3.reset(model, greenCPU);
      gf4.reset(model, redCPU);
      model.addScoreKeeper(sk);
      model.beginPlaying();
      if (model.numSequences(greenCPU) >= 2 && model.numSequences(redCPU) >= 2) {
        throw new IllegalStateException("Both won but somehow nobody did");
      }
    }
    System.out.println(sk.displayResults());

   }
}
