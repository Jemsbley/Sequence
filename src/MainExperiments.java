import java.util.List;
import java.util.Random;

import game.board.BasicCard;
import game.board.GameBoard;
import game.board.GamePosition;
import game.board.StandardBoardType;
import game.controller.GameMove;
import game.controller.MockController;
import game.controller.SequenceController;
import game.enums.CardSuit;
import game.enums.CardValue;
import game.enums.GameChip;
import game.model.PlayableSequenceModel;
import game.model.SequenceModel;
import game.view.GameFrame;

public class MainExperiments {

  public static void main(String[] args) {
    PlayableSequenceModel model = new SequenceModel();
    GameBoard def = new StandardBoardType();
    SequenceController blueGuy = new MockController(GameChip.BLUE);
    SequenceController redGuy = new MockController(GameChip.RED);

    model.initializeGame(def, List.of(blueGuy, redGuy), new Random(1));
    GameFrame gf = new GameFrame(model, blueGuy);
    GameFrame gf2 = new GameFrame(model, redGuy);
    gf.setVisible(true);
    gf2.setVisible(true);
    model.playToCell(new GameMove(new GamePosition(3, 8), 1));
    model.playToCell(new GameMove(new GamePosition(4, 0), 0));
    gf.redraw();
    gf2.redraw();

   }
}
