package game.scorekeeper;

import game.enums.GameChip;

public class TwoPlayerScoreKeeper implements ScoreKeeper{

  private int p1wins = 0;
  private int p2wins = 0;
  private int ties = 0;

  private GameChip p1 = GameChip.NONE;
  private GameChip p2 = GameChip.NONE;;

  public void addPlayer(GameChip player) {
    if (p1.equals(GameChip.NONE)) {
      p1 = player;
    } else if (p2.equals(GameChip.NONE)) {
      p2 = player;
    } else {
      throw new IllegalStateException("This tracker is full");
    }
  }

  public void increment(GameChip winner) {
    if (winner.equals(GameChip.NONE)) {
      ties += 1;
    } else if (winner.equals(p1)) {
      p1wins += 1;
    } else if (winner.equals(p2)) {
      p2wins += 1;
    } else {
      throw new IllegalArgumentException("Somehow nobody won and there was no tie: "
              + p1 + ", " + p2 + ": " + winner);
    }
  }

  @Override
  public String displayResults() {
    return "Team " + p1.name() + ": " + p1wins + " , Team "
            + p2.name() + ": " + p2wins + ", Ties: " + ties;
  }

}
