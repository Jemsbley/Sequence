package game.controller;

import java.util.Objects;

import game.enums.GameChip;
import game.model.PlayableSequenceModel;
import game.scorekeeper.ScoreKeeper;
import game.view.GameView;

public class HumanController implements SequenceController {

  private final GameChip team;
  private final PlayableSequenceModel model;
  private GameView view;

  public HumanController(PlayableSequenceModel model, GameChip team) {
    this.model = Objects.requireNonNull(model);
    this.team = team;
  }

  @Override
  public void addView(GameView view) {
    this.view = Objects.requireNonNull(view);
  }

  @Override
  public void deadCard(int cardIdx) {
    try {
      this.model.deadCard(cardIdx);
    } catch (IllegalStateException e) {
      this.view.popUp(e.getMessage());
    }
  }

  @Override
  public void receiveGameOver(GameChip winner) {
    if (winner.equals(GameChip.NONE)) {
      this.view.popUp("It's a tie");
    } else if (winner.equals(this.team)) {
      this.view.popUp("You win");
    } else {
      this.view.popUp("You lose");
    }
  }

  @Override
  public void beginTurn(PlayableSequenceModel model) {
    // Unnecessary for a human player
  }

  @Override
  public void receiveMove(GameMove moveToPlay) {
    this.model.playToCell(moveToPlay);
    try {
    } catch (IllegalArgumentException e) {
      this.view.popUp(e.getMessage());
    }
  }

  @Override
  public GameChip getTeam() {
    return this.team;
  }

  @Override
  public boolean usesMouse() {
    return true;
  }


}
