package game.controller;

import game.enums.GameChip;
import game.model.PlayableSequenceModel;

public class MockController implements SequenceController {

  private final GameChip team;

  public MockController(GameChip team) {
    this.team = team;
  }

  @Override
  public void beginTurn(PlayableSequenceModel model) {

  }

  @Override
  public void receiveMove(GameMove moveToPlay) {

  }

  @Override
  public GameChip getTeam() {
    return this.team;
  }
}
