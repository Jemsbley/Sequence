package game.controller;

import java.util.Objects;

import game.algorithms.SequenceAlgorithm;
import game.enums.GameChip;
import game.model.PlayableSequenceModel;
import game.scorekeeper.ScoreKeeper;
import game.view.GameView;

public class AlgorithmController implements SequenceController {

  private final SequenceAlgorithm cpu;
  private PlayableSequenceModel gameModel;
  private GameChip team;
  private GameView view;
  private ScoreKeeper tracker;

  public AlgorithmController(SequenceAlgorithm cpu, PlayableSequenceModel model, GameChip team) {
    this.cpu = Objects.requireNonNull(cpu);
    this.gameModel = Objects.requireNonNull(model);
    this.team = team;
  }


  @Override
  public void beginTurn(PlayableSequenceModel model) {
    this.cpu.beginTurn(this.gameModel, this);
  }

  @Override
  public void receiveMove(GameMove moveToPlay) {
    this.gameModel.playToCell(moveToPlay);
  }

  @Override
  public GameChip getTeam() {
    return this.team;
  }

  @Override
  public boolean usesMouse() {
    return false;
  }

  @Override
  public void addView(GameView view) {
    this.view = Objects.requireNonNull(view);
  }

  @Override
  public void deadCard(int cardIdx) {
    this.gameModel.deadCard(cardIdx);
    if (!Objects.isNull(this.view)) {
      this.view.redraw();
    }
  }

  @Override
  public void receiveGameOver(GameChip winner) {
    // not needed
  }

}
